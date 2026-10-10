import { AfterViewInit, Component, OnDestroy, OnInit, inject } from '@angular/core';
import * as L from 'leaflet';
import markerIcon from 'leaflet/dist/images/marker-icon.png';
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png';
import markerShadow from 'leaflet/dist/images/marker-shadow.png';
import { EmployeeStatus } from './services/employee-status';
import { EmployeeStatusService } from './services/employee-status';
import { Subscription, timer } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { DecimalPipe,DatePipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Auth } from './services/auth';
import { Attendance } from './services/attendance';
import { AttendanceService } from './services/attendance';

const defaultIcon = L.icon({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,

  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
});

L.Marker.prototype.options.icon = defaultIcon;

@Component({
  selector: 'app-root',
  imports: [DecimalPipe,DatePipe,CommonModule,FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, AfterViewInit {
  attendances: Attendance[] = [];

private attendanceService = inject(AttendanceService);
  username = '';
password = '';
isLoggedIn = false;
loginError = '';
  private authService = inject(Auth);
  private employeeStatusService = inject(EmployeeStatusService);
  private cdr = inject(ChangeDetectorRef);
  private stationMarkers: L.Marker[] = [];
private geofenceCircles: L.Circle[] = [];
  employees: EmployeeStatus[] = [];

  insideCount = 0;
  outsideCount = 0;
  staleCount = 0;
  noLocationCount = 0;
  unassignedCount = 0;

  private map!: L.Map;

  private employeeMarkers: L.Marker[] = [];

  ngOnInit(): void {
  this.isLoggedIn = this.authService.isLoggedIn();

  if (this.isLoggedIn) {
    this.loadEmployees();
    this.loadAttendance();
  }
  }
  login(): void {
  this.loginError = '';

  this.authService.login(
    this.username,
    this.password
  ).subscribe({
    next: () => {
      this.isLoggedIn = true;
      this.password = '';
      this.loadEmployees();
    },

    error: (error) => {
      console.error('Login failed:', error);

      this.loginError = 'Invalid username or password.';
    }
  });
}
loadAttendance(): void {
  console.log('Loading attendance...');

  this.attendanceService.getAttendance().subscribe({
    next: (data) => {
      console.log('Attendance data:', data);
      this.attendances = data;
      this.cdr.detectChanges();
    },

    error: (error) => {
      console.error('Failed to load attendance:', error);
    }
  });
}

logout(): void {
  this.authService.logout();

  this.isLoggedIn = false;
  this.username = '';
  this.password = '';
  this.employees = [];

  this.insideCount = 0;
  this.outsideCount = 0;
  this.staleCount = 0;
  this.noLocationCount = 0;
  this.unassignedCount = 0;
}

  ngAfterViewInit(): void {
    this.initializeMap();
  }

  loadEmployees(): void {

    console.log('Loading employee status...');

    this.employeeStatusService.getEmployeeStatus().subscribe({

      next: (data) => {

        console.log('Employee data:', data);

        this.employees = data;

        this.calculateCounts();

        this.cdr.detectChanges();

        // Update map after employee data arrives
        setTimeout(() => {
          this.updateMap();
        }, 100);

      },

      error: (error) => {
        console.error(
          'Employee status request failed:',
          error
        );
      }

    });
  }


  calculateCounts(): void {

    this.insideCount = this.employees.filter(
      employee => employee.status === 'INSIDE'
    ).length;

    this.outsideCount = this.employees.filter(
      employee => employee.status === 'OUTSIDE'
    ).length;

    this.staleCount = this.employees.filter(
      employee => employee.status === 'STALE'
    ).length;

    this.noLocationCount = this.employees.filter(
      employee => employee.status === 'NO_LOCATION'
    ).length;

    this.unassignedCount = this.employees.filter(
      employee => employee.status === 'UNASSIGNED'
    ).length;
  }


  refresh(): void {
    this.loadEmployees();
  }


  getStatusClass(status: string): string {

    switch (status) {

      case 'INSIDE':
        return 'status-inside';

      case 'OUTSIDE':
        return 'status-outside';

      case 'STALE':
        return 'status-stale';

      case 'NO_LOCATION':
        return 'status-no-location';

      case 'UNASSIGNED':
        return 'status-unassigned';

      default:
        return '';
    }
  }


  private initializeMap(): void {

    this.map = L.map('map');

    L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        attribution:
          '&copy; OpenStreetMap contributors'
      }
    ).addTo(this.map);

    // Temporary default location
    this.map.setView(
      [21.7647, 72.1520],
      13
    );

    setTimeout(() => {
      this.map.invalidateSize();
    }, 100);
  }


  private updateMap(): void {

  if (!this.map) {
    return;
  }


  // Remove old employee markers
  this.employeeMarkers.forEach(marker => {
    this.map.removeLayer(marker);
  });

  this.employeeMarkers = [];


  // Remove old station markers
  this.stationMarkers.forEach(marker => {
    this.map.removeLayer(marker);
  });

  this.stationMarkers = [];


  // Remove old geofence circles
  this.geofenceCircles.forEach(circle => {
    this.map.removeLayer(circle);
  });

  this.geofenceCircles = [];


  for (const employee of this.employees) {

    /*
     * STATION
     */

    if (
      employee.station_latitude !== null &&
      employee.station_latitude !== undefined &&
      employee.station_longitude !== null &&
      employee.station_longitude !== undefined
    ) {

      const stationLatitude = employee.station_latitude;
      const stationLongitude = employee.station_longitude;


      // Station marker
      const stationMarker = L.marker([
        stationLatitude,
        stationLongitude
      ], {
        icon: defaultIcon
      }).addTo(this.map);


      stationMarker.bindPopup(`
        <strong>⛽ ${employee.station ?? 'Fuel Station'}</strong><br>
        Geofence: ${employee.geofence_radius_meters ?? 0} m
      `);


      this.stationMarkers.push(stationMarker);


      /*
       * GEOFENCE CIRCLE
       */

      if (
        employee.geofence_radius_meters !== null &&
        employee.geofence_radius_meters !== undefined
      ) {

        const circle = L.circle(
          [stationLatitude, stationLongitude],
          {
            radius: employee.geofence_radius_meters,
            weight: 2,
            fillOpacity: 0.15
          }
        ).addTo(this.map);


        this.geofenceCircles.push(circle);
      }
    }


    /*
     * EMPLOYEE
     */

    if (
      employee.latitude === null ||
      employee.latitude === undefined ||
      employee.longitude === null ||
      employee.longitude === undefined
    ) {
      continue;
    }


    const latitude = employee.latitude;
    const longitude = employee.longitude;


    const employeeMarker = L.marker([
      latitude,
      longitude
    ], {
      icon: defaultIcon
    }).addTo(this.map);


    employeeMarker.bindPopup(`
      <strong>👤 ${employee.employee_name}</strong><br>
      Employee ID: ${employee.employee_id}<br>
      Station: ${employee.station ?? 'Not assigned'}<br>
      Distance:
      ${
        employee.distance_meters !== null
          ? employee.distance_meters + ' m'
          : 'N/A'
      }<br>
      Status: ${employee.status}
    `);


    this.employeeMarkers.push(employeeMarker);
  }


  /*
   * FIT MAP TO ALL MARKERS
   */

  const allLayers = [
    ...this.employeeMarkers,
    ...this.stationMarkers
  ];


  if (allLayers.length > 0) {

    const group = L.featureGroup(allLayers);

    this.map.fitBounds(
      group.getBounds().pad(0.2)
    );
  }
  }
}
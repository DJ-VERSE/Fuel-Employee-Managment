import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EmployeeStatus {
  employee_id: string;
  employee_name: string;

  station: string | null;

  station_latitude?: number | null;
  station_longitude?: number | null;

  latitude?: number | null;
  longitude?: number | null;

  distance_meters: number | null;

  geofence_radius_meters?: number | null;

  status:
    | 'INSIDE'
    | 'OUTSIDE'
    | 'STALE'
    | 'NO_LOCATION'
    | 'UNASSIGNED';

  last_updated: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeStatusService {

  private http = inject(HttpClient);

  private apiUrl = 'http://127.0.0.1:8000/api/employee-status/';

  getEmployeeStatus(): Observable<EmployeeStatus[]> {
    return this.http.get<EmployeeStatus[]>(this.apiUrl);
  }
}
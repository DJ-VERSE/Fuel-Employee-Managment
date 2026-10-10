from django.shortcuts import render

# Create your views here.
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from .serializers import EmployeeSerializer, RegisterSerializer

from .models import Employee
from .serializers import EmployeeSerializer


class EmployeeViewSet(viewsets.ModelViewSet):

    queryset = Employee.objects.all()
    serializer_class = EmployeeSerializer
    permission_classes = [IsAuthenticated]
    
    

from django.utils import timezone
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAdminUser
from rest_framework.response import Response

from .models import Employee
from locations.models import Location
from locations.views import calculate_distance

@api_view(["POST"])
@permission_classes([])
def register(request):

    serializer = RegisterSerializer(data=request.data)

    if serializer.is_valid():
        employee = serializer.save()

        return Response(
            {
                "message": "Registration successful.",
                "employee_id": employee.employee_id,
                "username": employee.user.username,
            },
            status=201
        )

    return Response(
        serializer.errors,
        status=400
    )

@api_view(["GET"])
@permission_classes([IsAdminUser])
def employee_status(request):
    employees = Employee.objects.filter(
        is_active=True
    ).select_related("user", "station")

    results = []

    for employee in employees:
        station = employee.station

        latest_location = (
            Location.objects
            .filter(employee=employee)
            .order_by("-recorded_at")
            .first()
        )

        # Employee without an assigned station
        if station is None:
            results.append({
                "employee_id": employee.employee_id,
                "employee_name": employee.user.username,
                "station": None,
                "status": "UNASSIGNED",
                "last_updated": None,
                "distance_meters": None,
            })
            continue

        # Employee has not sent any location yet
        if latest_location is None:
            results.append({
                "employee_id": employee.employee_id,
                "employee_name": employee.user.username,
                "station": station.name,
                "latitude": None,
                "longitude": None,
                "distance_meters": None,
                "geofence_radius_meters": station.geofence_radius,
                "status": "NO_LOCATION",
                "last_updated": None,
            })
            continue

        distance = calculate_distance(
            station.latitude,
            station.longitude,
            latest_location.latitude,
            latest_location.longitude,
        )

        # Don't treat an old GPS reading as a current location.
        age_seconds = (
            timezone.now() - latest_location.recorded_at
        ).total_seconds()

        if age_seconds > 300:
            current_status = "STALE"
        else:
            current_status = (
                "INSIDE"
                if distance <= station.geofence_radius
                else "OUTSIDE"
            )

        results.append({
            "employee_id": employee.employee_id,
            "employee_name": employee.user.username,

            "station": station.name,
            "station_latitude": float(station.latitude),
            "station_longitude": float(station.longitude),

            "latitude": float(latest_location.latitude),
            "longitude": float(latest_location.longitude),

            "distance_meters": round(distance, 2),
            "geofence_radius_meters": station.geofence_radius,

            "status": current_status,
            "last_updated": latest_location.recorded_at,
        })

    return Response(results)
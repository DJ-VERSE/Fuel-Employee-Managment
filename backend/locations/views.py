
import math

from rest_framework import status, viewsets
from rest_framework.decorators import action
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from .models import Location
from .serializers import LocationSerializer
from employees.models import Employee


def calculate_distance(lat1, lon1, lat2, lon2):
    """Calculate distance between two GPS coordinates in meters."""
    earth_radius = 6_371_000

    lat1 = math.radians(float(lat1))
    lon1 = math.radians(float(lon1))
    lat2 = math.radians(float(lat2))
    lon2 = math.radians(float(lon2))

    delta_lat = lat2 - lat1
    delta_lon = lon2 - lon1

    a = (
        math.sin(delta_lat / 2) ** 2
        + math.cos(lat1)
        * math.cos(lat2)
        * math.sin(delta_lon / 2) ** 2
    )

    a = max(0.0, min(1.0, a))
    return 2 * earth_radius * math.asin(math.sqrt(a))


class LocationViewSet(viewsets.ModelViewSet):
    serializer_class = LocationSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        # Employees can view only their own location records.
        # Staff can view all records.
        if self.request.user.is_staff:
            return Location.objects.select_related(
                "employee", "employee__station"
            ).order_by("-recorded_at")

        return Location.objects.filter(
            employee__user=self.request.user
        ).order_by("-recorded_at")

    def create(self, request, *args, **kwargs):
        try:
            latitude = float(request.data["latitude"])
            longitude = float(request.data["longitude"])
        except (KeyError, TypeError, ValueError):
            return Response(
                {"error": "Valid latitude and longitude are required."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        if (
            not math.isfinite(latitude)
            or not math.isfinite(longitude)
            or not -90 <= latitude <= 90
            or not -180 <= longitude <= 180
        ):
            return Response(
                {"error": "Coordinates are outside valid ranges."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        employee = Employee.objects.filter(
            user=request.user,
            is_active=True,
        ).select_related("station").first()

        if employee is None:
            return Response(
                {"error": "No active employee profile is assigned to this user."},
                status=status.HTTP_403_FORBIDDEN,
            )

        station = employee.station

        if station is None:
            return Response(
                {"error": "This employee has no assigned fuel station."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        distance = calculate_distance(
            station.latitude,
            station.longitude,
            latitude,
            longitude,
        )

        inside = distance <= station.geofence_radius
        location_status = "INSIDE" if inside else "OUTSIDE"

        data = {
            "employee": employee.id,
            "latitude": latitude,
            "longitude": longitude,
            "accuracy": request.data.get("accuracy"),
            "status": location_status,
        }

        serializer = self.get_serializer(data=data)
        serializer.is_valid(raise_exception=True)
        location = serializer.save()

        return Response(
            {
                "message": "Location recorded successfully.",
                "employee_id": employee.employee_id,
                "station": station.name,
                "distance_meters": round(distance, 2),
                "geofence_radius_meters": station.geofence_radius,
                "status": location.status,
                "recorded_at": location.recorded_at,
            },
            status=status.HTTP_201_CREATED,
        )
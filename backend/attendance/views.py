from django.shortcuts import render

# Create your views here.
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from django.utils import timezone
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response

from employees.models import Employee
from .models import Attendance
from locations.models import Location

from .models import Attendance
from .serializers import AttendanceSerializer


class AttendanceViewSet(viewsets.ModelViewSet):

    queryset = Attendance.objects.all().order_by("-date")
    serializer_class = AttendanceSerializer
    permission_classes = [IsAuthenticated]


@api_view(["POST"])
@permission_classes([IsAuthenticated])
def check_out(request):

    employee = Employee.objects.filter(
        user=request.user,
        is_active=True
    ).first()

    if employee is None:
        return Response(
            {"error": "Employee profile not found."},
            status=403
        )

    latest_location = (
        Location.objects
        .filter(employee=employee)
        .order_by("-recorded_at")
        .first()
    )

    if latest_location is None:
        return Response(
            {"error": "No location data available. Please send your location first."},
            status=400
        )

    if latest_location.status != "OUTSIDE":
        return Response(
            {
                "error": "Check-out is allowed only when you are outside the station.",
                "status": latest_location.status
            },
            status=403
        )

    today = timezone.localdate()

    attendance = Attendance.objects.filter(
        employee=employee,
        date=today
    ).first()

    if attendance is None:
        return Response(
            {"error": "No attendance record found for today."},
            status=400
        )

    if attendance.check_in is None:
        return Response(
            {"error": "Employee has not checked in today."},
            status=400
        )

    if attendance.check_out is not None:
        return Response(
            {
                "message": "Already checked out.",
                "check_out": attendance.check_out
            }
        )

    attendance.check_out = timezone.now()
    attendance.save()

    return Response(
        {
            "message": "Check-out successful.",
            "employee_id": employee.employee_id,
            "date": str(today),
            "check_in": attendance.check_in,
            "check_out": attendance.check_out
        },
        status=200
    )


    
@api_view(["POST"])
@permission_classes([IsAuthenticated])
def check_in(request):

    employee = Employee.objects.filter(
        user=request.user,
        is_active=True
    ).first()

    if employee is None:
        return Response(
            {"error": "Employee profile not found."},
            status=403
        )

    latest_location = (
        Location.objects
        .filter(employee=employee)
        .order_by("-recorded_at")
        .first()
    )

    if latest_location is None:
        return Response(
            {"error": "No location data available. Please send your location first."},
            status=400
        )

    if latest_location.status != "INSIDE":
        return Response(
            {
                "error": "Check-in is allowed only when you are inside the station.",
                "status": latest_location.status
            },
            status=403
        )

    today = timezone.localdate()

    attendance, created = Attendance.objects.get_or_create(
        employee=employee,
        date=today
    )

    if attendance.check_in is not None:
        return Response(
            {
                "message": "Already checked in.",
                "check_in": attendance.check_in
            }
        )

    attendance.check_in = timezone.now()
    attendance.save()

    return Response(
        {
            "message": "Check-in successful.",
            "employee_id": employee.employee_id,
            "date": str(today),
            "check_in": attendance.check_in
        },
        status=201
    )
    
@api_view(["GET"])
@permission_classes([IsAuthenticated])
def attendance_list(request):

    if request.user.is_staff:

        attendances = Attendance.objects.select_related(
            "employee",
            "employee__user"
        ).order_by("-date", "-check_in")

        results = []

        for attendance in attendances:
            results.append({
                "id": attendance.id,
                "employee": attendance.employee.id,
                "employee_id": attendance.employee.employee_id,
                "employee_name": attendance.employee.user.username,
                "date": attendance.date,
                "check_in": attendance.check_in,
                "check_out": attendance.check_out,
            })

        return Response(results)

    # Normal employee can see only their own attendance
    employee = Employee.objects.filter(
        user=request.user,
        is_active=True
    ).first()

    if employee is None:
        return Response(
            {"error": "Employee profile not found."},
            status=403
        )

    attendances = Attendance.objects.filter(
        employee=employee
    ).order_by("-date", "-check_in")

    results = []

    for attendance in attendances:
        results.append({
            "id": attendance.id,
            "employee": attendance.employee.id,
            "employee_id": attendance.employee.employee_id,
            "employee_name": attendance.employee.user.username,
            "date": attendance.date,
            "check_in": attendance.check_in,
            "check_out": attendance.check_out,
        })

    return Response(results)
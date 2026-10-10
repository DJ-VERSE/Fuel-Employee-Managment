from rest_framework.routers import DefaultRouter
from .views import EmployeeViewSet, employee_status, register



from django.urls import path
from rest_framework.routers import DefaultRouter

from .views import EmployeeViewSet, employee_status

router = DefaultRouter()
router.register(
    r"employees",
    EmployeeViewSet,
    basename="employee",
)

urlpatterns = [
    path("employee-status/", employee_status, name="employee-status"),
    path("register/", register, name="register"),
] + router.urls
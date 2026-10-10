from django.urls import path
from .views import check_in, check_out, attendance_list

urlpatterns = [
    path("check-in/", check_in, name="check-in"),
    path("check-out/", check_out, name="check-out"),
    path("list/", attendance_list, name="attendance-list"),
]
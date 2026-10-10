from django.db import models

# Create your models here.
from django.db import models
from django.contrib.auth.models import User
from stations.models import FuelStation


class Employee(models.Model):

    user = models.OneToOneField(
        User,
        on_delete=models.CASCADE
    )

    phone = models.CharField(max_length=15)

    station = models.ForeignKey(
        FuelStation,
        on_delete=models.SET_NULL,
        null=True,
        blank=True,
        related_name="employees"
    )

    employee_id = models.CharField(
        max_length=50,
        unique=True
    )

    is_active = models.BooleanField(default=True)

    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.employee_id} - {self.user.username}"
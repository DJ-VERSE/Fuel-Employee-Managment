from django.db import models

# Create your models here.
from django.db import models
from employees.models import Employee


class Attendance(models.Model):

    employee = models.ForeignKey(
        Employee,
        on_delete=models.CASCADE,
        related_name="attendance"
    )

    date = models.DateField()

    check_in = models.DateTimeField(
        null=True,
        blank=True
    )

    check_out = models.DateTimeField(
        null=True,
        blank=True
    )

    def __str__(self):
        return f"{self.employee} - {self.date}"
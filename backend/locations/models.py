from django.db import models

# Create your models here.
from django.db import models
from employees.models import Employee


class Location(models.Model):

    STATUS_CHOICES = [
        ("INSIDE", "Inside"),
        ("OUTSIDE", "Outside"),
    ]

    employee = models.ForeignKey(
        Employee,
        on_delete=models.CASCADE,
        related_name="locations"
    )

    latitude = models.DecimalField(
        max_digits=15,
        decimal_places=7
    )

    longitude = models.DecimalField(
        max_digits=15,
        decimal_places=7
    )

    accuracy = models.FloatField(
        null=True,
        blank=True
    )

    status = models.CharField(
        max_length=10,
        choices=STATUS_CHOICES
    )

    recorded_at = models.DateTimeField(
        auto_now_add=True
    )

    def __str__(self):
        return f"{self.employee} - {self.status}"
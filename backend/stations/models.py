

# Create your models here.
from django.db import models


class FuelStation(models.Model):
    name = models.CharField(max_length=150)

    address = models.TextField()

    latitude = models.DecimalField(
        max_digits=15,
        decimal_places=7
    )

    longitude = models.DecimalField(
        max_digits=15,
        decimal_places=7
    )

    geofence_radius = models.PositiveIntegerField(
        default=200,
        help_text="Geofence radius in meters"
    )

    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.name
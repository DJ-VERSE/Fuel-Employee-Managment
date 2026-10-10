from rest_framework import serializers
from .models import Employee
from rest_framework import serializers
from django.contrib.auth.models import User
from .models import Employee


class EmployeeSerializer(serializers.ModelSerializer):

    username = serializers.CharField(
        source="user.username",
        read_only=True
    )

    station_name = serializers.CharField(
        source="station.name",
        read_only=True
    )

    class Meta:
        model = Employee
        fields = [
            "id",
            "employee_id",
            "username",
            "phone",
            "station",
            "station_name",
            "is_active",
            "created_at",
        ]
        
class RegisterSerializer(serializers.Serializer):

    username = serializers.CharField(max_length=150)
    password = serializers.CharField(write_only=True, min_length=6)
    phone = serializers.CharField(max_length=15)
    employee_id = serializers.CharField(max_length=50)

    def validate_username(self, value):
        if User.objects.filter(username=value).exists():
            raise serializers.ValidationError(
                "Username already exists."
            )
        return value

    def validate_employee_id(self, value):
        if Employee.objects.filter(employee_id=value).exists():
            raise serializers.ValidationError(
                "Employee ID already exists."
            )
        return value

    def create(self, validated_data):

        user = User.objects.create_user(
            username=validated_data["username"],
            password=validated_data["password"]
        )

        employee = Employee.objects.create(
            user=user,
            employee_id=validated_data["employee_id"],
            phone=validated_data["phone"],
        )

        return employee        
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Attendance {
  id: number;
  employee: number;
  employee_id?: string;
  employee_name?: string;
  date: string;
  check_in: string | null;
  check_out: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {

  private http = inject(HttpClient);

  private apiUrl =
    'http://127.0.0.1:8000/api/attendance/list/';

  getAttendance(): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(
      this.apiUrl
    );
  }
}
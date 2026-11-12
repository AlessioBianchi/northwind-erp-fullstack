import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { EmployeesService } from '../../service/employees.service';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile.component.html'
})
export class ProfileComponent {
  private employeesService = inject(EmployeesService);
  private destroyRef = inject(DestroyRef);

  username = inject(AuthService).getUsernameLogged();
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  validationError = '';
  isSaved = false;
  isSaving = false;

  changePassword(): void {
    this.isSaved = false;
    this.validationError = '';

    if (this.newPassword.length < 8) {
      this.validationError = 'The new password must be at least 8 characters.';
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.validationError = 'The new passwords do not match.';
      return;
    }

    this.isSaving = true;
    this.employeesService.changeMyPassword(this.currentPassword, this.newPassword)
      .pipe(finalize(() => this.isSaving = false), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.isSaved = true;
          this.currentPassword = '';
          this.newPassword = '';
          this.confirmPassword = '';
        },
        error: () => {}
      });
  }
}

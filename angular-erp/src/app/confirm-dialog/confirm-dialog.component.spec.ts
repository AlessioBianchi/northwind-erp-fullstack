import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmDialogComponent } from './confirm-dialog.component';
import { ConfirmDialogService } from '../service/confirm-dialog.service';

describe('ConfirmDialogComponent', () => {
  let fixture: ComponentFixture<ConfirmDialogComponent>;
  let service: ConfirmDialogService;
  let element: HTMLElement;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmDialogComponent);
    service = TestBed.inject(ConfirmDialogService);
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  function button(label: string): HTMLButtonElement {
    return Array.from(element.querySelectorAll('button')).find(b => b.textContent?.trim() === label)!;
  }

  it('renders nothing until a confirmation is requested', () => {
    expect(element.querySelector('.modal')).toBeNull();
  });

  it('shows the title and message', () => {
    service.confirm('Delete this product?');
    fixture.detectChanges();
    expect(element.querySelector('.modal-title')?.textContent).toContain('Confirm deletion');
    expect(element.querySelector('.modal-body')?.textContent).toContain('Delete this product?');
  });

  it('resolves true on Confirm and closes', async () => {
    const choice = service.confirm('Delete?');
    fixture.detectChanges();
    button('Confirm').click();
    expect(await choice).toBe(true);
    fixture.detectChanges();
    expect(element.querySelector('.modal')).toBeNull();
  });

  it('resolves false on Cancel', async () => {
    const choice = service.confirm('Delete?');
    fixture.detectChanges();
    button('Cancel').click();
    expect(await choice).toBe(false);
  });

  it('resolves false on Escape', async () => {
    const choice = service.confirm('Delete?');
    fixture.detectChanges();
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    expect(await choice).toBe(false);
  });

  it('resolves false on a backdrop click', async () => {
    const choice = service.confirm('Delete?');
    fixture.detectChanges();
    (element.querySelector('.modal') as HTMLElement).click();
    expect(await choice).toBe(false);
  });

  it('a new request cancels the pending one', async () => {
    const first = service.confirm('First?');
    service.confirm('Second?');
    expect(await first).toBe(false);
    service.answer(false);
  });
});

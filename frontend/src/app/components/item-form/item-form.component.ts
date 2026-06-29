import { Component, EventEmitter, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ItemService } from '../../services/item.service';

@Component({
  selector: 'app-item-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="glass-panel form-container">
      <h3>Add New Item</h3>
      <div class="input-group">
        <input 
          type="text" 
          [(ngModel)]="newItemName" 
          placeholder="Enter item name..." 
          (keyup.enter)="createItem()"
        />
        <button class="btn btn-primary" (click)="createItem()" [disabled]="!newItemName.trim() || isSubmitting">
          {{ isSubmitting ? 'Adding...' : 'Add Item' }}
        </button>
      </div>
    </div>
  `,
  styles: [`
    .form-container {
      padding: 1.5rem;
      margin-bottom: 2rem;
    }
    h3 { margin-top: 0; margin-bottom: 1rem; color: var(--accent-color); font-weight: 600; }
    .input-group { display: flex; gap: 1rem; }
    input { flex: 1; }
    
    @media (max-width: 600px) {
      .input-group { flex-direction: column; }
    }
  `]
})
export class ItemFormComponent {
  @Output() itemAdded = new EventEmitter<void>();
  newItemName = '';
  isSubmitting = false;

  constructor(private itemService: ItemService, private cdr: ChangeDetectorRef) {}

  createItem() {
    if (!this.newItemName.trim() || this.isSubmitting) return;
    this.isSubmitting = true;
    this.itemService.createItem({ name: this.newItemName.trim() }).subscribe({
      next: () => {
        this.newItemName = '';
        this.isSubmitting = false;
        this.itemAdded.emit();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error creating item', err);
        this.isSubmitting = false;
        this.cdr.markForCheck();
      }
    });
  }
}

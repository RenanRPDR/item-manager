import { Component, EventEmitter, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ItemService } from '../../services/item.service';

@Component({
  selector: 'app-item-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="input-group">
      <input 
        type="text" 
        [(ngModel)]="newItemName" 
        placeholder="Enter new item name..." 
        (keyup.enter)="createItem()"
      />
      <button class="btn btn-primary" (click)="createItem()" [disabled]="!newItemName.trim() || isSubmitting">
        {{ isSubmitting ? 'Adding...' : '+ Add Item' }}
      </button>
    </div>
  `,
  styles: [`
    .input-group { display: flex; gap: 0.5rem; align-items: center; }
    input { min-width: 220px; }
    
    @media (max-width: 600px) {
      .input-group { flex-direction: column; width: 100%; align-items: stretch; }
      input { width: 100%; }
      button { width: 100%; }
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

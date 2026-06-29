import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ItemService } from '../../services/item.service';
import { Item, PageResponse } from '../../models/item.model';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-item-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="glass-panel list-container">
      <div class="list-header">
        <h3>Items</h3>
        <input 
          type="text" 
          placeholder="Search items..." 
          [(ngModel)]="searchTerm"
          (ngModelChange)="onSearchChange($event)"
          class="search-input"
        />
      </div>

      <div class="table-container">
        <table *ngIf="pageData?.content?.length; else noData">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th class="actions">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of pageData?.content">
              <td><span class="id-badge">#{{item.id}}</span></td>
              <td class="item-name">{{item.name}}</td>
              <td class="actions">
                <button class="btn btn-danger btn-sm" (click)="deleteItem(item.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
        <ng-template #noData>
          <div class="no-data">No items found matching your criteria.</div>
        </ng-template>
      </div>

      <div class="pagination" *ngIf="pageData && pageData.totalPages > 1">
        <button class="btn" [disabled]="pageData.first" (click)="loadPage(pageData.number - 1)">Previous</button>
        <span class="page-info">Page {{pageData.number + 1}} of {{pageData.totalPages}} <span class="total-badge">({{pageData.totalElements}} items)</span></span>
        <button class="btn" [disabled]="pageData.last" (click)="loadPage(pageData.number + 1)">Next</button>
      </div>
    </div>
  `,
  styles: [`
    .list-container { padding: 1.5rem; }
    .list-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 1rem; }
    h3 { margin: 0; color: var(--text-color); font-weight: 600; font-size: 1.25rem; }
    .search-input { min-width: 250px; }
    .table-container { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; text-align: left; }
    th, td { padding: 1rem; border-bottom: 1px solid var(--glass-border); }
    th { font-weight: 600; color: rgba(255,255,255,0.5); text-transform: uppercase; font-size: 0.8rem; letter-spacing: 0.05em; }
    tr { transition: background-color 0.2s ease; }
    tr:hover { background-color: rgba(255,255,255,0.03); }
    .id-badge { background: rgba(59, 130, 246, 0.2); color: #93c5fd; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.85rem; font-weight: 500; }
    .item-name { font-weight: 500; }
    .actions { text-align: right; }
    .btn-sm { padding: 0.4rem 0.8rem; font-size: 0.85rem; }
    .no-data { text-align: center; padding: 3rem; color: rgba(255,255,255,0.5); font-weight: 500; }
    .pagination { display: flex; justify-content: space-between; align-items: center; margin-top: 1.5rem; }
    .page-info { font-size: 0.95rem; color: var(--text-color); font-weight: 500; }
    .total-badge { color: rgba(255,255,255,0.5); }
    .btn[disabled] { opacity: 0.3; cursor: not-allowed; }
    .btn[disabled]:hover { transform: none; background: rgba(255,255,255,0.1); color: var(--text-color); border: none; }
    
    @media (max-width: 600px) {
      .list-header { flex-direction: column; align-items: stretch; }
      .search-input { min-width: 100%; }
      .pagination { flex-direction: column; gap: 1rem; }
    }
  `]
})
export class ItemListComponent implements OnInit {
  pageData: PageResponse<Item> | null = null;
  searchTerm = '';
  currentPage = 0;
  pageSize = 10;
  
  private searchSubject = new Subject<string>();

  constructor(private itemService: ItemService, private cdr: ChangeDetectorRef) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.currentPage = 0;
      this.loadItems();
    });
  }

  ngOnInit() {
    this.loadItems();
  }

  refresh() {
    this.currentPage = 0;
    this.loadItems();
  }

  loadItems() {
    this.itemService.getItems(this.currentPage, this.pageSize, this.searchTerm)
      .subscribe(data => {
        this.pageData = data;
        this.cdr.markForCheck();
      });
  }

  loadPage(page: number) {
    this.currentPage = page;
    this.loadItems();
  }

  onSearchChange(term: string) {
    this.searchSubject.next(term);
  }

  deleteItem(id: string) {
    if(confirm('Are you sure you want to delete this item?')) {
      this.itemService.deleteItem(id).subscribe(() => {
        if (this.pageData?.content.length === 1 && this.currentPage > 0) {
          this.currentPage--;
        }
        this.loadItems();
        this.cdr.markForCheck();
      });
    }
  }
}

import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ItemListComponent } from './components/item-list/item-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ItemListComponent],
  template: `
    <div class="app-container">
      <header>
        <h1>Item Manager</h1>
      </header>
      
      <main>
        <app-item-list #itemList></app-item-list>
      </main>
    </div>
  `,
  styles: [`
    .app-container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 3rem 1.5rem;
    }
    header {
      margin-bottom: 3rem;
      text-align: center;
      animation: fadeInDown 0.6s ease-out;
    }
    h1 {
      font-size: 3rem;
      margin: 0;
      background: linear-gradient(135deg, #93c5fd, #3b82f6);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      font-weight: 600;
      letter-spacing: -0.02em;
    }
    p {
      color: rgba(255,255,255,0.6);
      margin-top: 0.5rem;
      font-size: 1.1rem;
      font-weight: 300;
    }
    
    @keyframes fadeInDown {
      from { opacity: 0; transform: translateY(-20px); }
      to { opacity: 1; transform: translateY(0); }
    }
  `]
})
export class AppComponent {
}

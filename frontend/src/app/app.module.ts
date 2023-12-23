import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {NgxPaginationModule} from 'ngx-pagination';

import { AppComponent } from './app.component';
import { QuicksearchComponent } from './quicksearch/quicksearch.component';
import { AdvancedSearchComponent } from './advanced-search/advanced-search.component';
import { AuthorSearchComponent } from './author-search/author-search.component';
import { RouterModule, Routes } from '@angular/router';
import{ HttpClientModule} from '@angular/common/http';
import { TitleSearchComponent } from './title-search/title-search.component';

const appRoutes: Routes = [
  {path:'quicksearch', component: QuicksearchComponent},
  {path:'advancedsearch', component: AdvancedSearchComponent},
  {path:'authorsearch', component: AuthorSearchComponent},
  {path:'titlesearch', component: TitleSearchComponent},
  {path:'', component: QuicksearchComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    QuicksearchComponent,
    AdvancedSearchComponent,
    AuthorSearchComponent,
    TitleSearchComponent
  ],
  imports: [
    NgxPaginationModule,
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

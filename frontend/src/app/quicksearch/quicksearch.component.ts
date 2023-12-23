import { ChangeDetectionStrategy, Component, OnInit, NgZone, ChangeDetectorRef } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-quicksearch',
  templateUrl: './quicksearch.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./quicksearch.component.scss']
})
export class QuicksearchComponent implements OnInit {

  p: number = 1;
  shouldIDisplayResults: boolean = false;

  results = [
    {
        "title": "",
        "authors": [
            ""
        ],
        "content": "",
        "image": ""
    }
]

  constructor(private router: Router,private httpClient: HttpClient, private ngZone: NgZone, private cd: ChangeDetectorRef) {
   }


  ngOnInit(): void {
  }

  onSubmit(form: NgForm): void {
    this.httpClient
      .get('http://localhost:8081/basicsearch/' + form.value.search)
      .subscribe(
        (response:any) => {
          this.results = response;
          this.shouldIDisplayResults = true;
          this.cd.detectChanges();
        },
        (error) => {
          console.log('Erreur ! : ' + error);
        }
      );

  }

  goToLink(url: string){
    window.open(url, "_blank");
}

}

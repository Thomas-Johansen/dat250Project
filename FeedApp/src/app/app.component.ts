import { Component } from '@angular/core';
import { ApiService } from './api.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'FeedApp';
  users : any[] = [];
  user : any;
  username: any;
  name: any;
  password: any;
  email: any;
  constructor(private feedApp:ApiService) {
    this.feedApp.getUsers().subscribe(data=>{
      console.warn(data)
      this.users = data
    })
  }
  public refreshUsers(){
    this.feedApp.getUsers().subscribe(data=>{
      this.users = data
    })
  }
  public getUser(username:string, password:string){
    this.feedApp.getUser(username, password).subscribe(data=>{
      this.user = data
    })
  }

}

import { Component } from '@angular/core';
import { Hero } from './hero';
import { Http, Headers, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/map';
import {ToastyService, ToastyConfig, ToastOptions, ToastData} from 'ng2-toasty';

@Component({
  // moduleId: module.id,
  selector: 'hero-form',
  // providers: [ CheckoutService ],
  templateUrl: 'hero-form.component.html'
})

export class HeroFormComponent {

  constructor(public http: Http, private toastyService:ToastyService, private toastyConfig: ToastyConfig) {
    this.toastyConfig.theme = 'material';
  }

  endpoint = 'http://localhost:8080/';
  wsEndpoint = 'ws://localhost:8080/notifications';

  // endpoint = 'http://api.twitter-rmi.com/';
  // wsEndpoint = 'ws://api.twitter-rmi.com:8080/notifications';

  webSocket = undefined;

  addToast(title, msg) {
        var toastOptions:ToastOptions = {
            title: title,
            msg: msg,
            showClose: true,
            timeout: 5000,
            theme: 'material',
            onAdd: (toast:ToastData) => {
                console.log('Toast ' + toast.id + ' has been added!');
            },
            onRemove: function(toast:ToastData) {
                console.log('Toast ' + toast.id + ' has been removed!');
            }
        };
        this.toastyService.info(toastOptions);
    }

  updateChat(msg) {
    let jsonMsg = JSON.parse(msg.data);
    this.addToast("@" + this.userHandle, jsonMsg.msg);
    let tweet = {user_handler: "twitter-rmi", body: jsonMsg.msg, verify: false};
    // this.timeline.unshift(tweet);
    console.log("[WEBSOCKETS] " + msg.data);
  }

  insert(targetId, message) {
    //window.toastr.info('Are you the 6 fingered man?')
    //alert("HOLITA TENÉS 1 NOTIFICACIÓN: " + message);
    //this.id(targetId).insertAdjacentHTML("afterbegin", message);
  }

  id(id) {
      return document.getElementById(id);
  }

  startWebSocket() {
    this.webSocket = new WebSocket(this.wsEndpoint);
    this.webSocket.onmessage = msg => this.updateChat(msg);
    this.webSocket.onclose = _ => {
      this.addToast("Twitter RMI", "Cerrada la conexión de notificaciones.");
      if (this.userHandle != '') {
        this.startWebSocket();
      }
    }
    // this.webSocket.send(this.userHandle);
    this.webSocket.onopen = _ => this.webSocket.send("handle:" + this.userHandle);
    console.log("WebSocket conectado.");
  }

  userHandle = '';
  userBio = '';
  loginError = false;

  login() {
    console.log("Holita desde login antes de hacer post");
    let body = 'handle=' + this.model.name + '&password=' + this.model.alterEgo;
    // console.log(body);
    let headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');
    return this.http.get(this.endpoint + 'login/' + this.model.name + '/' + this.model.alterEgo, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        if (data.error == 'login incorrecto') {
          console.log('recibida respuesta de login incorrecto');
          this.loginError = true;
        } else {
          this.loginError = false;
          this.token = data.token,
          this.userHandle = data.handle,
          this.newFollows = data.follows,
          this.userBio = data.bio,
          console.log('Login bien, token recibido -> ', data),
          this.addToast("@" + this.userHandle, "Bienvenido a Twitter RMI.")
          this.getTimeline()
          this.startWebSocket()
        }
      },
      error => console.log('Login no valido')
    );
  }

  doRegister() {
    console.log("Holita desde login antes de hacer post");
    let body = 'handle=' + this.model.name + '&password=' + this.model.alterEgo;
    // console.log(body);
    let headers = new Headers();
    return this.http.get(this.endpoint + 'register/' + this.regmodel.name + '/' + this.regmodel.password1, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        if (data.error == 'login incorrecto') {
          console.log('recibida respuesta de registro incorrecto');
        } else {
        this.token = data.token,
        this.userHandle = data.handle,
        this.userBio = data.bio,
        console.log('Login bien, token recibido -> ', data),
        this.startWebSocket(),
        this.getTimeline()
        // si luego hace logout, que salga la ventana de login, no de register
        this.register = false;
      }
      },
      error => console.log('Login no valido')
    );
  }

  tweet = '';

  isFollow(userHandle) {
    let i;
    for (i = 0; i < this.newFollows.length; i++) {
      if (this.newFollows[i] == userHandle) {
        return true;
      }
    }
    return false;
  }

  submitStatus() {
    console.log("Holita desde submitStatus() antes de hacer post");
    let body = this.tweet;
    // console.log(body);
    let headers = new Headers();
    headers.append('Content-Type', 'application/x-www-form-urlencoded');
    this.isSendingTweet = true;
    return this.http.post(this.endpoint + 'status/' + this.token, body, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        this.tweet = '';
        console.log('submitStatus() bien -> ', data),
        // this.addToast("@" + this.userHandle, "Tu tweet se ha enviado correctamente.")
        this.getTimeline()
      }
    );
  }

  pmTo = '';
  pmBody = '';

  submitPM() {
    console.log("Holita desde submitPM() antes de hacer post");
    let body = '{"to": "' + this.pmTo + '", "body": "' + this.pmBody + '"}';
    console.log(body);
    let headers = new Headers();
    headers.append('Content-Type', 'application/json; charset=utf-8');
    this.isSendingTweet = true;
    return this.http.post(this.endpoint + 'pm/' + this.token, body, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        this.pmTo = '';
        this.pmBody = '';
        console.log('submitPM() bien -> ', data)
        // this.addToast("@" + this.userHandle, "Tu tweet se ha enviado correctamente.")
        // this.getTimeline()
      }
    );
  }

  meetNewPeople = [];
  loadingMeetNewPeople = false;

  getMeetNewPeople() {
    this.loadingMeetNewPeople = true;
    this.meetNewPeople = [];
    console.log("Holita desde getMeetNewPeople() antes de hacer cositas");
    let headers = new Headers();
    return this.http.get(this.endpoint + 'meetnewpeople/' + this.token, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        this.meetNewPeople = data;
        console.log('meetNewPeople bien -> ', data);
        this.loadingMeetNewPeople = false;
      }
    );
  }

  loadingPrivateMessages = false
  privateMessages = []

  getPrivateMessages() {
    this.loadingPrivateMessages = true
    this.privateMessages = []
    console.log("getPrivateMessages() -> before GET")
    let headers = new Headers()
    return this.http.get(this.endpoint + 'getpm/' + this.token, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        this.privateMessages = data
        this.loadingPrivateMessages = false
        console.log("getPrivateMessages() -> " + data)
      }
    )
  }

  newFollows = [];

  logout() {
    var handle = this.userHandle;
    if (this.webSocket != undefined) {
      this.webSocket.close();
    }
    this.timeline = [];
    this.model.name = '';
    this.model.alterEgo = '';
    this.userHandle = '';
    this.newFollows = [];
    this.token = '';
    this.section = 0;
    this.addToast("@" + handle, "Te has desconectado correctamente.");
  }

  unfollow(username) {
    console.log("Holita desde unfollow() antes de hacer post");
    let headers = new Headers();
    return this.http.get(this.endpoint + 'unfollow/' + this.token + '/' + username, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        console.log('unfollow(' + username + ') bien -> ', data)
        let i;
        for (i = 0; i < this.newFollows.length; i++) {
          if (this.newFollows[i] == username) {
            this.newFollows.splice(i, 1);
            break;
          }
        }
        console.log('newFollows actualizado -> ' + this.newFollows);
        this.addToast("@" + this.userHandle, "Has dejado de seguir a @" + username + ".")
      }
    );
  }

  follow(username) {
    console.log("Holita desde follow() antes de hacer post");
    let headers = new Headers();
    return this.http.get(this.endpoint + 'follow/' + this.token + '/' + username, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        console.log('follow(' + username + ') bien -> ', data)
        this.newFollows.push(username);
        console.log('newFollows actualizado -> ' + this.newFollows);
        this.addToast("@" + this.userHandle, "Has seguido a @" + username + ".")
      }
    );
  }

  timeline = [];
  section = 0;

  avatar(handler) {
    if (handler == 'jrevillas') {
      return 'https://pbs.twimg.com/profile_images/742374129849016321/pLrSck8g_400x400.jpg';
    }
    else if (handler == 'twitter-rmi') {
      return 'https://pbs.twimg.com/profile_images/767879603977191425/29zfZY6I_400x400.jpg';
    }
    else if (handler == 'miguel_jimg' || handler == 'miguel' || handler == 'mjimenez') {
      return 'https://pbs.twimg.com/profile_images/2919845983/53a9fbe37e7dbb8abd19d7f6df1f8891_400x400.jpeg';
    }
    else if (handler == 'rfgpl' || handler == 'rfernandez' || handler == 'rafa') {
      return 'https://pbs.twimg.com/profile_images/736138563541213184/9MTfRQ0R_400x400.jpg';
    }
    else if (handler == 'javiruiz' || handler == 'javiruiz01') {
      return 'https://pbs.twimg.com/profile_images/795311430971039744/0ZfOasx5_400x400.jpg';
    }
    else if (handler == 'mnunez95' || handler == 'mnunez') {
      return 'https://pbs.twimg.com/profile_images/447758390022778880/WGnmYCUg_400x400.jpeg';
    }
    else if (handler == 'tomholland1996') {
      return 'https://pbs.twimg.com/profile_images/607638914166366208/14epsJiB_400x400.jpg';
    }
    else if (handler == 'clara') {
      return 'https://pbs.twimg.com/profile_images/460887351754252288/0IaxZbi0_400x400.jpeg';
    }
    else {
      return 'https://pbs.twimg.com/media/CLI0ZQmUkAA9int.png';
    }
  }

  isSendingTweet = false;
  pendingTimeline = false;

  getTimeline() {
    console.log("Holita desde login antes de hacer post");
    let body = 'handle=' + this.model.name + '&password=' + this.model.alterEgo;
    // console.log(body);
    let headers = new Headers();
    this.pendingTimeline = true;
    // headers.append('Content-Type', 'application/x-www-form-urlencoded');
    return this.http.get(this.endpoint + 'timeline/' + this.token, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        this.timeline = data
        // console.log('Recibida timeline -> ', data),
        console.table(data)
        this.isSendingTweet = false
        this.pendingTimeline = false
      },
      error => console.log('Error al recibir la timeline')
    );
  }

  dashboardData = {user:{name:"Javier Revillas",balance:496},folders:[{name:"Timeline",files:[],id:0},{name:"Mis followers",files:[],id:1},{name:"Gente a la que sigo",files:[],id:2}]};

  requestDashboard() {
    console.log('[INFO] requestDashboard()');
    let body = '{"token":"' + this.token + '"}';
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.post('https://jre.villas/daetsiinf/dashboard', body, {headers: headers})
    .map(response => response.json())
    .subscribe(
      data => {
        console.log(data);
        this.dashboardData = data;
        this.selected = data.folders[0].id;
      },
      error => console.log('[ERROR] requestDashboard()')
    );
  }

  sayToken() {
    console.log(this.selected);
  }

  powers = ['Administrador', 'Super Flexible', 'Super Hot', 'Weather Changer'];

  model = new Hero(18, '', this.powers[0], '');

  regmodel = {name: "", email: "", password1: "", password2: ""};

  register = false;

  selected = 0;

  token = '';

  balance = 4.96;

  tokenFinal = 'tok_19MFadD2EhydZ03U4W1gqhmM';

  openCheckout() {
    var handler = (<any>window).StripeCheckout.configure({
      key: 'pk_test_lLEvnIx7CQij5Rc9YQy3RHW1',
      locale: 'auto',
      token: function (token: any) {
        this.tokenFinal = token.id;
        console.log(token.id);
        // this.postToken(token);
      }
    });
    handler.open({
      name: 'DA ETSI Informáticos',
      description: 'Recarga de saldo',
      amount: 300
    });
  }

  prints() { return ((this.balance * 100) / 4).toFixed(); }

  checkout(pages) { this.balance = parseFloat((this.balance - (pages * 0.04)).toFixed(2)); }

  submitted = false;

  hello = true;

  openModal() { document.getElementById("openModalButton").click(); }

  onSubmit() { this.submitted = true; }

  newHero() { this.model = new Hero(42, '', ''); }

}

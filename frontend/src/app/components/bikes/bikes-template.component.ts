import { CheckoutComponent } from '../checkout/checkout.component';
import { Component, OnInit, ViewChild, AfterViewInit, ViewChildren, QueryList } from '@angular/core';
import { Bikes } from 'src/app/interfaces/bikes';
import { environment } from 'src/environments/environment';
import { bikes_typeService } from '../../services/bikes_type.service';
import { BikesService } from '../../services/bikes.service';
import { GoogleMap, MapInfoWindow, MapMarker } from '@angular/google-maps';
import { HttpErrorResponse } from '@angular/common/http';



@Component({
  selector: 'app-bike-template',
  templateUrl: './bikes-template.component.html',
  styleUrls: ['./bikes-template.component.css']
})
export class BikesTemplateComponent implements OnInit, AfterViewInit {
  public apikey = environment.google_maps_api;
  public bikes: Bikes[];
  
  public mapOptions: google.maps.MapOptions = {
    //center: { lat: 44.439663, lng: 26.096306 }, 
    center: {lat: 22, lng: 12},
    zoom : 4
  }

  // TODO:
  
  // CENTER MAP => FIT BOUNDS
  
  //public markers: google.maps.Marker;
  
  
  constructor(private BikesService: BikesService, private CheckoutComponent: CheckoutComponent) {this.bikes = []; }

  ngOnInit(): void { console.log(this.map); this.get_all_bikes();
  }

  ngAfterViewInit(): void {
    console.log(this.map);
  }

  @ViewChild(GoogleMap) map!: GoogleMap;
  @ViewChild(MapInfoWindow) infoWindow!: MapInfoWindow;

  protected get_coords(marker: Bikes){
    let lat = Number(marker.latitudine);
    let long = Number(marker.longitudine);
    let coordinates = new google.maps.LatLng(lat, long);
    return coordinates;

  }

  public get_all_bikes() : any {
    /*this.BikesService.getBikes().subscribe({
      error: (e) => console.log(e),
      complete: this.bikes = Response
    })*/
    this.BikesService.getBikes().subscribe(
      (Response: Bikes[]) => {this.bikes = Response; console.log(Response); },
      (error: HttpErrorResponse) => {alert(error.message); console.log(error);}
    )
    
  }

  openInfoWindow(marker: MapMarker, location: google.maps.LatLng) {
    
    this.infoWindow.open(marker, true);
  }

  @ViewChildren(MapInfoWindow)
  infoWindowsView!: QueryList<MapInfoWindow>;

  openInfoWindow2(marker: MapMarker, windowIndex: number) {
  /// stores the current index in forEach
  let curIdx = 0;
  this.infoWindowsView.forEach((window: MapInfoWindow) => {
    if (windowIndex === curIdx) {
      window.open(marker);
      curIdx++;
    } else {
      window.close();
      curIdx++;
    }
  });
  }
/*
  public pay_this(name1: string, amount1: number) {
    //window.open("localhost:4200/bikes", "_blank");
    this.CheckoutComponent.pay(name1, amount1*10);
    console.log(name1, amount1);
  }
*/
  public pay_this(id_bike: string) {
    //window.open("localhost:4200/bikes", "_blank");
    var id2 = Number(id_bike);
    this.CheckoutComponent.pay(id2);
    //console.log(name1, amount1);
  }

}

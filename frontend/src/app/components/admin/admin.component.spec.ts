import {
  ComponentFixture,
  TestBed,
  tick,
  fakeAsync,
} from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { Router } from '@angular/router';
import { AdminPage } from './admin.component';
import { UserService } from 'src/app/services/user.service';
import { AuthService } from 'src/app/services/auth.service';
import { IdTokenResult, User } from 'firebase/auth';

describe('AdminPage', () => {
  let component: AdminPage;
  let fixture: ComponentFixture<AdminPage>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    // Create spy objects for services
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUsers']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getCurrentUser']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      declarations: [AdminPage],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminPage);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch users on init', fakeAsync(() => {
    // mock token:

    const mockIdToken: IdTokenResult = {
      authTime: '1644211200', // Replace with an actual timestamp (e.g., seconds since epoch)
      claims: {
        // Add claims as needed
        email: 'john.doe@example.com',
        isAdmin: true,
      },
      expirationTime: '1644297600', // Replace with an actual timestamp (e.g., seconds since epoch)
      issuedAtTime: '1644214800', // Replace with an actual timestamp (e.g., seconds since epoch)
      signInProvider: 'firebase',
      signInSecondFactor: null, // Set to a second factor if applicable
      token: 'mock-id-token',
    };

    // Mock data for the user service response
    const mockUser: User = {
      displayName: 'John Doe',
      email: 'john.doe@example.com',
      emailVerified: true,
      isAnonymous: false,
      metadata: {
        creationTime: '2022-01-01T12:00:00Z', // Replace with an actual timestamp
        lastSignInTime: '2022-01-02T12:00:00Z', // Replace with an actual timestamp
      },

      phoneNumber: '+1234567890',
      photoURL: 'https://example.com/avatar.jpg', // Replace with an actual URL
      providerData: [
        {
          displayName: 'Google User',
          email: 'john.doe@gmail.com',
          phoneNumber: null,
          photoURL: 'https://google.com/avatar.jpg', // Replace with an actual URL
          providerId: 'google.com',
          uid: 'google-uid',
        },
        // Add more provider data entries as needed
      ],
      providerId: 'firebase',
      refreshToken: 'mock-refresh-token', // Replace with an actual refresh token
      tenantId: null, // Set to the actual tenant ID if applicable
      uid: 'mock-uid', // Replace with an actual UID

      delete: () => Promise.resolve(), // Mock delete method
      getIdToken: () => Promise.resolve('mock-id-token'), // Mock getIdToken method
      getIdTokenResult: () => Promise.resolve(mockIdToken), // Mock getIdTokenResult method
      reload: () => Promise.resolve(), // Mock reload method
      toJSON: () => ({}), // Mock toJSON method
    };

    // Mock AuthService's getCurrentUser to return a user with getIdToken method
    authServiceSpy.getCurrentUser.and.returnValue(mockUser);

    // Mock UserService's getUsers method to return an observable of mockUsers
    userServiceSpy.getUsers.and.returnValue(of(mockUser));

    // Call ngOnInit
    component.ngOnInit();
    tick();

    // Check if the component's users property is updated with the mockUsers
    expect(component.users).toEqual(jasmine.objectContaining(mockUser));
    // Check if the userService.getUsers method was called with the expected token
    expect(userServiceSpy.getUsers).toHaveBeenCalledWith('mock-id-token');
  }));

  it('should navigate to profile on error', fakeAsync(() => {
    // Mock AuthService's getCurrentUser to return a user with getIdToken method
    const mockIdToken: IdTokenResult = {
      authTime: '1644211200', // Replace with an actual timestamp (e.g., seconds since epoch)
      claims: {
        // Add claims as needed
        email: 'john.doe@example.com',
        isAdmin: false,
      },
      expirationTime: '1644297600', // Replace with an actual timestamp (e.g., seconds since epoch)
      issuedAtTime: '1644214800', // Replace with an actual timestamp (e.g., seconds since epoch)
      signInProvider: 'firebase',
      signInSecondFactor: null, // Set to a second factor if applicable
      token: 'mock-id-token',
    };

    // Mock data for the user service response
    const mockUser: User = {
      displayName: 'John Doe',
      email: 'john.doe@example.com',
      emailVerified: true,
      isAnonymous: false,
      metadata: {
        creationTime: '2022-01-01T12:00:00Z', // Replace with an actual timestamp
        lastSignInTime: '2022-01-02T12:00:00Z', // Replace with an actual timestamp
      },

      phoneNumber: '+1234567890',
      photoURL: 'https://example.com/avatar.jpg', // Replace with an actual URL
      providerData: [
        {
          displayName: 'Google User',
          email: 'john.doe@gmail.com',
          phoneNumber: null,
          photoURL: 'https://google.com/avatar.jpg', // Replace with an actual URL
          providerId: 'google.com',
          uid: 'google-uid',
        },
        // Add more provider data entries as needed
      ],
      providerId: 'firebase',
      refreshToken: 'mock-refresh-token', // Replace with an actual refresh token
      tenantId: null, // Set to the actual tenant ID if applicable
      uid: 'mock-uid', // Replace with an actual UID

      delete: () => Promise.resolve(), // Mock delete method
      getIdToken: () => Promise.resolve('mock-id-token'), // Mock getIdToken method
      getIdTokenResult: () => Promise.resolve(mockIdToken), // Mock getIdTokenResult method
      reload: () => Promise.resolve(), // Mock reload method
      toJSON: () => ({}), // Mock toJSON method
    };

    authServiceSpy.getCurrentUser.and.returnValue(mockUser);

    // Mock UserService's getUsers method to return an observable with an error
    userServiceSpy.getUsers.and.returnValue(
      new Observable((observer) => {
        observer.error('Mock error');
      })
    );

    // Call ngOnInit
    component.ngOnInit();
    tick();

    // Check if the router.navigate method was called with the expected route
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/profile']);
  }));
});

// Rect.aidl
package com.example.server;

// Declare Rect so AIDL can find it and knows that it implements
// the parcelable protocol.
parcelable Rect;

// In Android 10 (API level 29 or higher), you can define Parcelable objects directly in AIDL
/*parcelable Rect {
    int left;
    int top;
    int right;
    int bottom;
}*/

# Firebase Realtime Database Setup Instructions

## Fix "Permission Denied" Error

The "Permission denied" error occurs because Firebase Realtime Database security rules need to be configured. Follow these steps:

### Step 1: Open Firebase Console
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **tooltechinnovators**

### Step 2: Navigate to Realtime Database
1. In the left sidebar, click on **Realtime Database**
2. If you haven't created a database yet, click **Create Database**
3. Choose your location (preferably close to your users)
4. Start in **test mode** (we'll update the rules next)

### Step 3: Update Security Rules
1. Click on the **Rules** tab at the top
2. Replace the existing rules with the following:

```json
{
  "rules": {
    "carts": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    },
    "orders": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    }
  }
}
```

3. Click **Publish** to save the rules

### Step 4: Verify Authentication is Enabled
1. Go to **Authentication** in the left sidebar
2. Make sure **Email/Password** sign-in method is enabled
3. If not, enable it and save

### What These Rules Do:
- **carts/$userId**: Users can only read/write their own cart data
- **orders/$userId**: Users can only read/write their own orders
- `auth.uid` ensures only authenticated users can access their own data

### Testing:
After updating the rules:
1. Make sure you're logged in to the app
2. Try adding an item to cart
3. Try viewing your orders

The permission denied error should be resolved!






# Walkthrough - Unified Address Management

I have unified the address management experience between the "Account" and "Checkout" sections. Both screens now share the same data source, logic, and UI components.

## Changes Made

### Frontend - Android

#### [AccountAddressAdapter.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/account/AccountAddressAdapter.java)
- **Selection Mode Support**: Added a `selectionMode` flag to show/hide RadioButtons and change item click behavior.
- **Improved UI**: Enhanced the layout to show full recipient name, phone, and detailed address.
- **Card Highlighting**: Selected or default addresses are now highlighted with a pink background.

#### [CheckoutAddressFragment.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/commerce/CheckoutAddressFragment.java)
- **Unified ViewModel**: Switched from `CheckoutAddressViewModel` to `AccountViewModel`.
- **Unified Adapter**: Replaced `CheckoutAddressAdapter` with the newly enhanced `AccountAddressAdapter`.
- **Selection Flow**:
    1. Clicking an address now opens a confirmation dialog.
    2. Confirming will set that address as the **Global Default** via the Account API.
    3. The screen then automatically returns to the Checkout confirmation page with the new address selected.

#### [CheckoutFragment.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/commerce/CheckoutFragment.java)
- **Sync with Account**: Updated to use `AccountViewModel` to load the default address. This ensures that changes made in the Account section or the "Select other address" screen are immediately reflected here.

### Clean up
- **Deleted [CheckoutAddressAdapter.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/commerce/CheckoutAddressAdapter.java)**: This file is no longer needed as we use the unified `AccountAddressAdapter`.

## Verification Results

- **Data Consistency**: Both Account and Checkout now pull data from the same backend endpoint (`/api/account/address-book`).
- **UI Uniformity**: The list of addresses looks and behaves exactly the same in both sections of the app.
- **Selection Logic**: Setting a default in Account updates Checkout, and selecting an address in Checkout updates the default in Account.

# Implementation Plan - Unified Address Management

This plan outlines the changes to unify the address management pages in the Account and Checkout sections. Both pages will share the same data source, business logic, and UI components.

## User Review Required

> [!IMPORTANT]
> The `CheckoutAddressFragment` will now use the same API and logic as the `AccountAddressFragment`. Selecting an address in the Checkout flow will automatically set it as the **Global Default** address for the account.

## Proposed Changes

### [Frontend - Android]

#### [MODIFY] [AccountAddressAdapter.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/account/AccountAddressAdapter.java)
- Add `onAddressSelected(AddressDto address)` to `OnAddressActionListener`.
- Add a `selectionMode` property to toggle RadioButton visibility and item selection behavior.
- Update `onBindViewHolder` to show/hide the radio button based on `selectionMode`.
- Handle item clicks in selection mode to trigger `onAddressSelected`.

#### [MODIFY] [CheckoutAddressFragment.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/commerce/CheckoutAddressFragment.java)
- Replace `CheckoutAddressViewModel` with `AccountViewModel` for data loading and default address setting.
- Replace `CheckoutAddressAdapter` with `AccountAddressAdapter`.
- Set the adapter to `selectionMode(true)`.
- Update `observeViewModel` to listen to `accountViewModel.getAccountAddressesResult()`.
- Implement `onAddressSelected` to:
    1. Show a confirmation dialog.
    2. Call `accountViewModel.setDefaultAccountAddress()`.
- Observe `accountViewModel.getSetDefaultAccountAddressResult()`:
    - On success: Update `checkoutViewModel.setSelectedAddress()` and pop the fragment to return to the Checkout screen.

#### [MODIFY] [AccountAddressFragment.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/account/AccountAddressFragment.java)
- Update the `OnAddressActionListener` implementation to include the new `onAddressSelected` method (can be empty).

## Verification Plan

### Manual Verification
1. **Consistency Check**:
   - Go to "My Addresses" in Account.
   - Go to "Select other address" in Checkout.
   - Verify both lists look identical and have the same addresses.
2. **Selection Flow**:
   - In Checkout -> "Select other address", click an address.
   - Confirm selection in the dialog.
   - Verify it returns to Checkout with the selected address.
   - Return to Account -> "My Addresses" and verify that address is now the default one.
3. **Set Default Flow**:
   - In either page, click "Set Default".
   - Verify it updates immediately on both pages.

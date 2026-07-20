# Implementation Plan - Allow Email Accounts to Add Phone Number

This plan outlines the changes to allow users who registered with an email to add a phone number to their profile, while maintaining immutability for the primary registration information.

## User Review Required

> [!IMPORTANT]
> I will be making the primary registration method (Email or Phone) immutable in the UI. If a user registered by email, they can add/edit a phone number, but cannot change their registration email. Conversely, if they registered by phone, they can add/edit an email, but cannot change their registration phone.

## Proposed Changes

### [Backend]

#### [MODIFY] [account.controller.js](file:///D:/KanilaApp/backend/controllers/account.controller.js)
- Update `getProfileHub` to include `registrationChannel` (from `account.registration_channel`) in the response profile object.
- Ensure `patchMyProfile` correctly handles the `phone` field updates (uniqueness check and validation), similar to the existing email logic.

### [Frontend - Android]

#### [MODIFY] [ProfileHubDto.java](file:///D:/KanilaApp/frontend/app/src/main/java/com/example/frontend/data/model/account/ProfileHubDto.java)
- Add `registrationChannel` field to the `AccountInfo` nested class to store the registration method.

#### [MODIFY] [page_profile_overview.xml](file:///D:/KanilaApp/frontend/app/src/main/res/layout/page_profile_overview.xml)
- Add `ic_chevron_right` icons to the Email and Phone rows.
- Set IDs for these chevrons (e.g., `ivEmailChevron`, `ivPhoneChevron`) so their visibility can be toggled in code.

#### [MODIFY] [ProfileOverviewFragment.java](file:///D:/KanilaApp/frontend/app/src/main/java/ui/account/ProfileOverviewFragment.java)
- Update `bindData` to store the `registrationChannel` and toggle the visibility of the Email/Phone chevrons based on whether they are editable.
- Update `tvEmailValue` click listener:
    - If `registrationChannel` is "email", show a toast: "Email đăng ký không thể thay đổi".
    - Otherwise, allow editing.
- Update `tvPhoneValue` click listener:
    - If `registrationChannel` is "phone", show a toast: "Số điện thoại đăng ký không thể thay đổi".
    - Otherwise, allow adding/editing.
- Ensure the `phone` is correctly passed in the request body in `saveProfile`.

## Verification Plan

### Manual Verification
1. **Email-registered Account**:
    - Log in with an email account.
    - Go to Personal Profile.
    - Verify that the Email row has no chevron and shows a toast when clicked.
    - Verify that the Phone row has a chevron and opens an edit dialog when clicked.
    - Add a phone number and save. Verify the update is successful.
2. **Phone-registered Account**:
    - Log in with a phone account.
    - Go to Personal Profile.
    - Verify that the Phone row has no chevron and shows a toast when clicked.
    - Verify that the Email row has a chevron and opens an edit dialog when clicked.
    - Add an email and save. Verify the update is successful.

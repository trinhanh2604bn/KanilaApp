<<<<<<< HEAD
# KanilaFormControls

## Overview
KanilaFormControls is a reusable component group designed for the Kanila Beauty Commerce app. It provides a consistent, premium look and feel for inputs and buttons across all modules.

## Main Component: KanilaInputField
`KanilaInputField` is a master reusable input field that supports multiple modes and states.

### Supported Modes (kanilaMode)
- `text`: Default single-line text input.
- `password`: Password input with a visibility toggle.
- `multiline`: Multi-line text input for long messages.
- `search`: Input field with a leading search icon.
- `actionButton`: Combined input and button (e.g., for voucher codes).

### Supported States
- `default`: Normal state with border divider.
- `focused`: Highlighted border when the field is active.
- `error`: Red border and icon with an error message.
- `success`: Green border and icon with a success message.
- `disabled`: Grayed-out background and disabled interaction.

### Usage in XML
```xml
<ui.common.KanilaInputField
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:kanilaLabel="@string/label_email"
    app:kanilaHint="@string/hint_email"
    app:kanilaMode="text" />
```

### Usage in Java
```java
KanilaInputField inputEmail = findViewById(R.id.inputEmail);
inputEmail.setErrorState("Invalid email address");
inputEmail.setSuccessState("Voucher applied!");
```

## Button Styles
Reference these styles in your `Button` or `MaterialButton` widgets.

- `Widget.Kanila.Button.Primary`: Main CTA (Buy Now, Submit).
- `Widget.Kanila.Button.Secondary`: Secondary actions (Edit, View Policy).
- `Widget.Kanila.Button.Ghost`: Low-emphasis actions (Skip, Cancel).
- `Widget.Kanila.Button.Pill`: Compact choices or chips.
- `Widget.Kanila.Button.Icon`: Circular icon buttons.

## Design Rules
- **No Hardcoding**: Always use `@dimen`, `@string`, and `@color` resources.
- **Reuse**: Do not create custom input backgrounds or separate styles for form controls in feature modules.
- **Consistency**: Use the existing Kanila color palette and Nunito fonts.
- **Error UX**: Always show an error icon and message when in an error state.

## File Locations
- Java: `java/ui/common/KanilaInputField.java`
- Layout: `res/layout/view_kanila_input_field.xml`
- Styles: `res/values/styles.xml`
- Attributes: `res/values/attrs.xml`
- Icons: `res/drawable/ic_*`
- Backgrounds: `res/drawable/bg_kanila_input_*`
=======
# Kanila UI Components

## KanilaButtonInputView

### Purpose
`KanilaButtonInputView` is a self-contained, production-ready reusable component designed to provide a consistent input experience across the Kanila Beauty Commerce app. It combines a label, an input field, optional icons, and an optional action button into a single component to avoid manual assembly in feature layouts.

### Supported Modes
Set via the `app:kbiMode` attribute:
- `normal`: Standard text input.
- `password`: Input with integrated password visibility toggle.
- `search`: Input with search icon and clear text functionality.
- `action`: Input combined with an action button (e.g., "Apply" for vouchers).
- `multiline`: Expanded input for long text entries.

### Visual States
The component manages its visual states internally:
- **Default**: Standard border and background.
- **Focused**: Highlighted border using `@color/primary`.
- **Error**: Red border and message using `@color/error`.
- **Success**: Green border and message using `@color/success`.
- **Disabled**: Subtle background using `@color/background_sub`.

### XML Usage Examples

#### Action Mode (Voucher Code)
```xml
<ui.common.KanilaButtonInputView
    android:id="@+id/voucherInput"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:kbiLabel="@string/label_voucher"
    app:kbiHint="@string/hint_voucher_code"
    app:kbiActionText="@string/action_apply"
    app:kbiMode="action" />
```

#### Password Mode
```xml
<ui.common.KanilaButtonInputView
    android:id="@+id/passwordInput"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:kbiLabel="@string/label_password"
    app:kbiHint="@string/hint_password"
    app:kbiMode="password" />
```

### Java Usage Examples

#### Handling Action Click
```java
KanilaButtonInputView voucherInput = findViewById(R.id.voucherInput);
voucherInput.setOnActionClickListener(v -> {
    String code = voucherInput.getText();
    // Validate and apply voucher
});
```

#### Setting States Programmatically
```java
// Show Error
voucherInput.setError("Invalid voucher code");

// Show Success
voucherInput.setSuccess("Voucher applied!");

// Disable Input
voucherInput.setInputEnabled(false);
```

### Validation
Use `KanilaInputValidator` for standardized validation logic:
```java
if (!KanilaInputValidator.isEmailValid(email)) {
    emailInput.setError(KanilaInputValidator.getEmailError(context));
}
```

### Implementation Details
- **Location**: `java/ui/common/KanilaButtonInputView.java`
- **Layout**: `res/layout/view_kanila_button_input.xml`
- **Styling**: Borders and backgrounds are drawn programmatically using `GradientDrawable` to maintain consistency with app colors without excessive XML files.
- **Rules**: Business modules must use this component directly. Do not recreate separate input/button combinations.
>>>>>>> 4ba85578601a7f4e9fac3949e9a44f59354863e5

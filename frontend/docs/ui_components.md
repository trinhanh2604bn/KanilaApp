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

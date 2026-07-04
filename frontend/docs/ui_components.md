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

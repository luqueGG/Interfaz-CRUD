package com.solidtoxic.gui.component;

/**
 * Describes a single field in the RegisterForm.
 *
 * @param key       JSON key / column name (e.g. "ID_Toxicidad")
 * @param label     Human-readable label shown in the form
 * @param editable  Whether the operator can modify this field (PKs are not editable in MODIFY mode)
 * @param isPk      Whether this field is the primary key
 * @param maxLength Maximum allowed character length (0 = no limit / TEXT)
 * @param isLarge   Whether to render a TextArea instead of a TextField
 */
public record FieldDescriptor(
        String key,
        String label,
        boolean editable,
        boolean isPk,
        int maxLength,
        boolean isLarge
) {
    /** Convenience constructor for simple non-PK fields. */
    public FieldDescriptor(String key, String label, int maxLength) {
        this(key, label, true, false, maxLength, false);
    }

    /** Convenience constructor for TEXT / large fields. */
    public static FieldDescriptor large(String key, String label) {
        return new FieldDescriptor(key, label, true, false, 0, true);
    }

    /** Convenience constructor for PK fields. */
    public static FieldDescriptor pk(String key, String label, int maxLength) {
        return new FieldDescriptor(key, label, true, true, maxLength, false);
    }
}

package com.solidtoxic.gui.component;

/**
 * agregando fields para el formulario de la tabla
 * respecto a tablas que contengan claves foraneas
 */
public record FieldDescriptor(
        String key,
        String label,
        boolean editable,
        boolean isPk,
        int maxLength,
        boolean isLarge,
        String fkEndpoint,
        String fkLabelKey,
        String fkValueKey
) {
    /** Plain non-PK text field. */
    public FieldDescriptor(String key, String label, int maxLength) {
        this(key, label, true, false, maxLength, false, null, null, null);
    }

    /** TEXT / large field. */
    public static FieldDescriptor large(String key, String label) {
        return new FieldDescriptor(key, label, true, false, 0, true, null, null, null);
    }

    /** PK text field. */
    public static FieldDescriptor pk(String key, String label, int maxLength) {
        return new FieldDescriptor(key, label, true, true, maxLength, false, null, null, null);
    }

    /**
     * FK ComboBox field.
     *
     * @param key          The JSON key used when submitting (matches this table's FK column)
     * @param label        Label shown in the form
     * @param fkEndpoint   API path suffix, e.g. "region" → /api/v1/region?state=A
     * @param fkValueKey   JSON key in the referenced table that holds the value to submit
     * @param fkLabelKey   JSON key in the referenced table to show as display text
     */
    public static FieldDescriptor fk(String key, String label,
                                     String fkEndpoint, String fkValueKey, String fkLabelKey) {
        return new FieldDescriptor(key, label, true, false, 0, false, fkEndpoint, fkLabelKey, fkValueKey);
    }

    /** Convenience: same key as the value key (FK id field name matches local column). */
    public static FieldDescriptor fk(String key, String label, String fkEndpoint, String fkLabelKey) {
        return fk(key, label, fkEndpoint, key, fkLabelKey);
    }

    public boolean isFk() {
        return fkEndpoint != null;
    }
}

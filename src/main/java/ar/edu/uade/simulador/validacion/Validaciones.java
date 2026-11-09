package ar.edu.uade.simulador.validacion;

/** Patrones de validación reutilizados por la capa web. */
public final class Validaciones {

    /** Legajo: entre 7 y 15 dígitos numéricos, sin letras ni guiones. */
    public static final String PATRON_LEGAJO = "\\d{7,15}";

    /** Código de plan: letras, números, punto, guion bajo o guion medio (1 a 20 caracteres). */
    public static final String PATRON_CODIGO_PLAN = "[A-Za-z0-9._-]{1,20}";

    /**
     * Igual a PATRON_CODIGO_PLAN pero también acepta el valor vacío.
     * Se usa junto a {@code @NotBlank}: así, cuando el campo llega vacío, el
     * mensaje de error que gana es "no debe estar vacío" (el de @NotBlank) y
     * no el del formato, que confundiría al usuario.
     */
    public static final String PATRON_CODIGO_PLAN_O_VACIO = "^(" + PATRON_CODIGO_PLAN + ")?$";

    private Validaciones() {
    }
}

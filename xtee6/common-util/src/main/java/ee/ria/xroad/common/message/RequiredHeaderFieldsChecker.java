package ee.ria.xroad.common.message;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.annotation.XmlElement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import ee.ria.xroad.common.CodedException;

import static ee.ria.xroad.common.ErrorCodes.X_MISSING_HEADER_FIELD;
import static ee.ria.xroad.common.ErrorCodes.translateException;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
class RequiredHeaderFieldsChecker extends Listener {

    private final Class<?> clazz;

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        if (target.getClass().isAssignableFrom(clazz)) {
            try {
                checkRequiredFields(target);
            } catch (Exception e) {
                throw translateException(e);
            }
        }
    }

    static void checkRequiredFields(Object obj) throws Exception {
        for (Field field : getDeclaredFields(obj.getClass())) {
            XmlElement annotation = SoapUtils.getXmlElementAnnotation(field);
            if (annotation != null) {
                Object value = getValue(field, obj);

                if (annotation.required() && value == null) {
                    throw new CodedException(X_MISSING_HEADER_FIELD,
                            "Required field '%s' is missing",
                            annotation.name());
                }

                if (value != null && value instanceof ValidatableField) {
                    ((ValidatableField) value).validate();
                }
            }
        }
    }

    static Object getValue(Field field, Object obj) throws Exception {
        field.setAccessible(true); // the field might be private
        return field.get(obj);
    }

    private static List<Field> getDeclaredFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();

        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }

        return fields;
    }
}
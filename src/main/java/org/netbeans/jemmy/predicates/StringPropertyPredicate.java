package org.netbeans.jemmy.predicates;

public final class StringPropertyPredicate extends PropertyPredicate {
    public StringPropertyPredicate(String[] propNames, String[] results) {
        this(propNames, null, null, results);
    }

    public StringPropertyPredicate(String[] propNames, Object[][] params, Class[][] classes, String[] results) {
        super(propNames, params, classes, results);
    }

    @Override
    boolean checkProperty(Object value, Object etalon) {
        return value.toString().equals(etalon.toString());
    }
}

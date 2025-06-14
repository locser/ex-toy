package locser.toy.domain.specifications;

import org.springframework.data.jpa.domain.Specification;

public abstract class BaseSpecification<T> {
    protected Specification<T> byField(String fieldName, Object value) {
        return (root, query, cb) -> {
            if (value == null || (value instanceof Integer && (Integer) value == -1)) {
                return null;
            }
            return cb.equal(root.get(fieldName), value);
        };
    }

    protected Specification<T> byFieldLike(String fieldName, String value) {
        return (root, query, cb) -> {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get(fieldName)), "%" + value.toLowerCase() + "%");
        };
    }

    @SuppressWarnings("unchecked")
    protected <Y extends Comparable<? super Y>> Specification<T> byFieldBetween(String fieldName, Y min, Y max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }

            if (min != null && max != null) {
                return cb.between(root.get(fieldName), min, max);
            }

            if (min != null) {
                return cb.greaterThanOrEqualTo(root.get(fieldName), min);
            }

            return cb.lessThanOrEqualTo(root.get(fieldName), max);
        };
    }

}
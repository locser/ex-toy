package locser.toy.domain.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import locser.toy.domain.model.entity.Toy;

@Component
public class ToySpecification extends BaseSpecification<Toy> {
    public Specification<Toy> byFieldId(String field, Object value) {
        return byField(field, value);
    }

    public Specification<Toy> byStatus(Integer status) {
        return byField("status", status);
    }

    public Specification<Toy> byNameLike(String name) {
        return byFieldLike("name", name);
    }

    public Specification<Toy> byToyCondition(Long toyCondition) {
        return byField("toyCondition", toyCondition);
    }
}
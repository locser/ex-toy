package locser.toy.domain.validation;

import locser.toy.domain.exception.BadRequestException;

/**
 * Lớp tiện ích để kiểm tra tính hợp lệ của ID.
 */
public class IdValidator {

    /**
     * Kiểm tra xem ID có hợp lệ không (phải lớn hơn 0).
     *
     * @param id         ID cần kiểm tra
     * @param entityName Tên của entity
     * @throws BadRequestException nếu ID không hợp lệ
     */
    public static void validateId(Long id, String entityName) {
        if (id == null || (id <= 0 && id != -1)) {
            throw new BadRequestException(entityName + " ID phải lớn hơn 0");
        }
    }
}

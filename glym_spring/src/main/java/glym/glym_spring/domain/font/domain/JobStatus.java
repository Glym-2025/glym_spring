package glym.glym_spring.domain.font.domain;

public enum JobStatus {
    QUEUED, PENDING, PROCESSING, COMPLETED, FAILED;

    /**
     * 문자열을 받아 해당 JobStatus enum 상수를 반환합니다.
     *
     * @param status 문자열 상태 (예: "QUEUED", "PENDING")
     * @return 해당 JobStatus enum 상수
     * @throws IllegalArgumentException 유효하지 않은 상태 문자열일 경우
     */

    private final static String STATUS_CANNOT_BE_NULL = "상태는 널 값이면 안됩니다.";
    private final static String INVALID_STATUS="유효하지 상태값입니다.";

    public static JobStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException(STATUS_CANNOT_BE_NULL);
        }

        try {
            return JobStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(INVALID_STATUS);
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
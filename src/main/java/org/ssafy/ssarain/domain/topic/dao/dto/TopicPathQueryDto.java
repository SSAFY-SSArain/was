package org.ssafy.ssarain.domain.topic.dao.dto;

public record TopicPathQueryDto(
        Number targetTid,
        Number tid,
        Number pid,
        String name,
        Number using,
        Number depth
        ) {

    public Integer getTargetTid() {
        return targetTid.intValue();
    }

    public int getTid() {
        return tid.intValue();
    }

    public Integer getPid() {
        if (pid == null) {
            return null;
        }
        return pid.intValue();
    }

    public boolean isUsing() {
        return using.intValue() == 1;
    }
}

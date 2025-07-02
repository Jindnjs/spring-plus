package org.example.expert.domain.log.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.example.expert.domain.common.entity.Timestamped;

@Entity
public class Log extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String logMessage;

    protected Log(){}

    public static Log createLog(String logMessage){
        Log log = new Log();
        log.logMessage = logMessage;
        return log;
    }
}

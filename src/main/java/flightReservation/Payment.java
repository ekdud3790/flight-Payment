package flightReservation;

import javax.persistence.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flightReservation.config.kafka.KafkaProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long price;
    private String flightId;
    private String userId;
    private String status;

    @PostUpdate
    public void onPostUpdate(){
        String status = this.getStatus();
        System.out.println("==============onPostUpdate ====== status : "+ status);

        if(status.equals("paySucceeded")){
            PayCompleted paycompleted = new PayCompleted();

            paycompleted.setId(this.getId());
            paycompleted.setFlightId(this.getFlightId());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(paycompleted);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }


            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());


            /*BeanUtils.copyProperties(this, revsuccessed);
            revsuccessed.publishAfterCommit();*/
       }else if(status.equals("payFail")){
            PayFailed payfail = new PayFailed();

            payfail.setId(this.getId());
            payfail.setFlightId(this.getFlightId());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;

            try {
                json = objectMapper.writeValueAsString(payfail);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON format exception", e);
            }


            KafkaProcessor processor = Application.applicationContext.getBean(KafkaProcessor.class);
            MessageChannel outputChannel = processor.outboundTopic();

            outputChannel.send(MessageBuilder
                    .withPayload(json)
                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                    .build());


            /*BeanUtils.copyProperties(this, revsuccessed);
            revsuccessed.publishAfterCommit();*/
        }

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(String status){this.status = status;}
    public String getStatus(){return status;}



}

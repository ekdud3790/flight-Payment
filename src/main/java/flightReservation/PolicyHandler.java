package flightReservation;

import flightReservation.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PolicyHandler{
    @Autowired
    PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRequested_PayRequest(@Payload Requested requested){
        System.out.println("============================= Kafka : " + requested);

        if(requested.isMe()){
            System.out.println("=============================");
            System.out.println("requested");

            if(requested.getUserMoney()>=100){
                paymentRepository.findByflightId(requested.getFlightId())
                        .ifPresent(
                                payment -> {

                                        payment.setStatus("paySucceeded");
                                        paymentRepository.save(payment);
                                        System.out.println(": paySucceeded");
                                }
                        );
            }else{
                paymentRepository.findByflightId(requested.getFlightId())
                        .ifPresent(
                                payment -> {

                                    payment.setStatus("payFail");
                                    paymentRepository.save(payment);
                                    System.out.println(": payFail");
                                }
                        );
            }

        }
        System.out.println("=============================");
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRequested_CancelRequest(@Payload ResvCanceled resvCanceled){

        if(resvCanceled.isMe()){
            System.out.println("=============================");
            System.out.println("payCanceled");

            paymentRepository.findByflightId(resvCanceled.getFlightId())
                    .ifPresent(
                            payment -> {
                                payment.setStatus("payCanceled");
                                paymentRepository.save(payment);
                            }
                    )
            ;
            System.out.println("payCanceled");
            System.out.println("=============================");        }
    }
}

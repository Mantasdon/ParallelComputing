import java.util.concurrent.atomic.AtomicInteger;
//Mantas Donėla informatika
//Antrosios uždoties sprendimas
/* Dalykinė sritis - Producer gamina kazkokius resursus*/
/* Consumer panaudoja tuos resursus tik tda kai await ivyksta ir pasiekia resursai nurodyta skaiciu*/
class ClassicCounter {
    int counter = 0;
    public synchronized void advance() {
        counter++;
        notify();
    }

    public synchronized int read() {
        return counter;
    }

    public synchronized void await(int value) throws InterruptedException
    {
        while(this.counter < value) {
            wait();
        }
        System.out.println("await() finished");
    }
}

public class Main {
    public static void main(String[] args) {
        ClassicCounter counter = new ClassicCounter();
        int N = 10;
        int[] array = new int[10];
        Thread producer = new Thread(() -> {

               for(int i = 0; i < N; i ++){
                   array[i] = i;
                   System.out.println(array[i]);
                   counter.advance();
               }

        });

        Thread consumer = new Thread(() -> {
            try {


                while(!(counter.read() < array.length)){
                    counter.await(5);
                    System.out.println("Consumer consumed: " + counter.read());

                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer2 = new Thread(() -> {
            try {
                counter.await(10);
                System.out.println("Consumer2 consumed: " + counter.read());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producer.start();
        consumer.start();
        consumer2.start();
    }
}

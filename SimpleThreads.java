public class SimpleThreads {

    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };
            try {
                for (int i = 0; i < importantInfo.length; i++) {
                    Thread.sleep(4000);
                    threadMessage(importantInfo[i]);
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    private static class PrimeCalculator implements Runnable {
        public void run() {
            threadMessage("Iniciando calculo de numeros primos...");
            int count = 0;
            int number = 2;

            try {
                while (true) {
                    // Verifica se foi interrompida
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }

                    if (isPrime(number)) {
                        count++;
                        if (count % 1000 == 0) {
                            threadMessage("Primos encontrados ate agora: " + count + " | ultimo: " + number);
                        }
                    }
                    number++;
                }
            } catch (InterruptedException e) {
                threadMessage("Calculo interrompido! Primos encontrados: " + count);
            }
        }

        private boolean isPrime(int n) {
            if (n < 2) return false;
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // Tempo limite em milissegundos (padrão: 10 segundos)
        long patience = 10000;
        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        // --- Thread original ---
        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(new MessageLoop());
        t.start();

        threadMessage("Waiting for MessageLoop thread to finish");
        while (t.isAlive()) {
            threadMessage("Still waiting...");
            t.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {
                threadMessage("Tired of waiting!");
                t.interrupt();
                t.join();
            }
        }
        threadMessage("Finally!");

        threadMessage("Starting PrimeCalculator thread");
        long primeStartTime = System.currentTimeMillis();
        Thread primeThread = new Thread(new PrimeCalculator());
        primeThread.start();

        threadMessage("Waiting for PrimeCalculator thread...");
        while (primeThread.isAlive()) {
            threadMessage("PrimeCalculator still running...");
            primeThread.join(1000);
            if (((System.currentTimeMillis() - primeStartTime) > patience) && primeThread.isAlive()) {
                threadMessage("PrimeCalculator exceeded time limit! Interrupting...");
                primeThread.interrupt();
                primeThread.join();
            }
        }
        threadMessage("PrimeCalculator done!");
    }
}
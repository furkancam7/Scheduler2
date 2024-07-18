import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;


class Task {
    String name;
    long deadline;
    long duration;

    public Task(String name, long deadline, long duration) {
        this.name = name;
        this.deadline = deadline;
        this.duration = duration;
    }
}

public class Scheduler {
    private Task[] taskArray; // Array to hold tasks.
    private int taskCount; // Number of tasks in the scheduler.
    private long currentTime; // Current time in the scheduler.
    private static final int CAPACITY = 1000; // Maximum capacity of the scheduler.

    // Constructor initializes the task array, task count, and current time.
    public Scheduler() {
        taskArray = new Task[CAPACITY];
        taskCount = 0;
        currentTime = 0;
    }

    // Method to schedule a new task.
    public void scheduleTask(String name, long deadline, long duration) {
        Task newTask = new Task(name, deadline, duration); // Create a new task.
        taskArray[taskCount] = newTask; // Add the new task to the array.
        taskCount++; // Increment the task count.
        swim(taskCount - 1); // Ensure the heap property is maintained.
        System.out.println(currentTime + ": adding " + name + " with deadline " + deadline + " and duration " + duration);
    }

    // Method to run tasks until a specified time.
    public void run(long untilTime) {
        while (taskCount > 0 && currentTime < untilTime) { // Loop while there are tasks and current time is less than untilTime.
            Task currentTask = taskArray[0]; // Get the task with the earliest deadline.
            System.out.println(currentTime + ": busy with " + currentTask.name + " with deadline " + currentTask.deadline + " and duration " + currentTask.duration);

            long newTime = currentTime + currentTask.duration; // Calculate new time after task is done.
            if (newTime <= untilTime) { // If new time is within untilTime.
                currentTime = newTime; // Update current time.
                if (currentTime > currentTask.deadline) { // Check if the task is late.
                    System.out.println(currentTime + ": done with " + currentTask.name + " (late)");
                } else {
                    System.out.println(currentTime + ": done with " + currentTask.name);
                }
                removeMin(); // Remove the completed task.
            } else { // If task duration extends beyond untilTime.
                currentTask.duration -= (untilTime - currentTime); // Adjust the task duration.
                currentTime = untilTime; // Update current time to untilTime.
                System.out.println(currentTime + ": adding " + currentTask.name + " with deadline " + currentTask.deadline + " and duration " + currentTask.duration);
                sink(0); // Ensure the heap property is maintained.
                return; // Exit after adjusting the current task.
            }
        }
        currentTime = untilTime; // Update current time to untilTime.
    }

    // Method to maintain heap property from bottom to top.
    private void swim(int index) {
        while (index > 0 && taskArray[(index - 1) / 2].deadline > taskArray[index].deadline) {
            swap(index, (index - 1) / 2); // Swap with parent if parent has a later deadline.
            index = (index - 1) / 2; // Move up the heap.
        }
    }

    // Method to maintain heap property from top to bottom.
    private void sink(int index) {
        while (2 * index + 1 < taskCount) { // While there are children.
            int j = 2 * index + 1; // Left child.
            if (j + 1 < taskCount && taskArray[j].deadline > taskArray[j + 1].deadline) { // If right child exists and is smaller.
                j++;
            }
            if (taskArray[index].deadline <= taskArray[j].deadline) { // If parent is smaller than the smallest child.
                break;
            }
            swap(index, j); // Swap with the smallest child.
            index = j; // Move down the heap.
        }
    }

    // Method to remove the task with the earliest deadline.
    private void removeMin() {
        if (taskCount == 0) return; // If no tasks, do nothing.
        taskArray[0] = taskArray[--taskCount]; // Replace root with the last task.
        sink(0); // Restore heap property.
    }

    // Method to swap two tasks in the array.
    private void swap(int i, int j) {
        Task temp = taskArray[i];
        taskArray[i] = taskArray[j];
        taskArray[j] = temp;
    }

    // Main method to read tasks from a file and schedule/run them.
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(); // Create a new scheduler.

        try {
            File file = new File("src\\sampleinput1.txt"); // File containing tasks.
            Scanner scanner = new Scanner(file); // Scanner to read the file.

            while (scanner.hasNextLine()) { // While there are lines to read.
                String[] parts = scanner.nextLine().split(" "); // Split the line into parts.
                if (parts[0].equals("schedule")) { // If it's a schedule command.
                    String name = parts[1];
                    long deadline = Long.parseLong(parts[2]);
                    long duration = Long.parseLong(parts[3]);
                    scheduler.scheduleTask(name, deadline, duration); // Schedule the task.
                } else if (parts[0].equals("run")) { // If it's a run command.
                    long untilTime = Long.parseLong(parts[1]);
                    scheduler.run(untilTime); // Run the scheduler until the specified time.
                }
            }

            scanner.close(); // Close the scanner.
        } catch (FileNotFoundException e) { // If the file is not found.
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

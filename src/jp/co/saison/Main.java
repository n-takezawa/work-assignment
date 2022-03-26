package jp.co.saison;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    /**
     * 負担がなるべく均等になるように仕事を分担するプログラム
     *
     * @param args
     */
    public static void main(String[] args) {
        // 仕事する人
        List<String> clerks = Arrays.asList("A", "B", "C", "D");

        // 仕事
        List<Task> tasks = Arrays.asList(new Task("Studio全般", 125)
                , new Task("Studio for Web全般", 124)
                , new Task("ツール", 21)
                , new Task("スクリプトコンポーネント", 121)
                , new Task("コンバータ", 71)
                , new Task("データベース", 302)
                , new Task("ファイル", 148)
                , new Task("アプリケーション", 288)
                , new Task("ネットワーク", 97)
                , new Task("ディレクトリサービス", 67)
                , new Task("クラウド", 878)
                , new Task("ビッグデータ", 32)
                , new Task("HULFT", 10)
                , new Task("トリガー", 32));

        // 項目数が多い順に並べ替える
        tasks.sort((a, b) -> b.getItemTotal() - a.getItemTotal());

        final Map<String, List<Task>> assignedTasks = assign(tasks, clerks);

        for (String clerk : assignedTasks.keySet()) {
            System.out.printf("------- %s の担当 ----------%n", clerk);
            int total = 0;
            for (Task task : assignedTasks.get(clerk)) {
                System.out.println(task.getName() + " : " + task.getItemTotal());
                total = total + task.getItemTotal();
            }
            System.out.println("合計 : " + total);
        }

    }

    private static Map<String, List<Task>> assign(List<Task> tasks, List<String> clerks) {
        Map<String, List<Task>> assignedTasksMap = new HashMap<>();
        List<Task> notYetAssignedTasks = tasks;

        for (int i = 0; i < clerks.size(); i++) {
            int notAssignedQuantity = notYetAssignedTasks.stream().mapToInt(Task::getItemTotal).sum();
            int remainedClerkTotal = clerks.size() - i; // 未アサインの人数
            int targetQuantity = notAssignedQuantity / remainedClerkTotal; // 未アサインの人数で残りの量を割る

            List<Task> assignedTasks = assign(notYetAssignedTasks, targetQuantity);
            notYetAssignedTasks = createNextNotYetAssignedTasks(notYetAssignedTasks, assignedTasks);

            String clerk = clerks.get(i);
            System.out.printf("------- %s の目標 %d項目 ----------%n", clerk, targetQuantity);
            assignedTasksMap.put(clerk, assignedTasks);
        }

        if (notYetAssignedTasks.size() > 0) {
            System.out.println("!! ====== 未割当のタスクが残った! ========");
            for (Task task : notYetAssignedTasks) {
                System.out.printf("------- %s %d ----------%n", task.getName(), task.getItemTotal());
            }
        }
        return assignedTasksMap;
    }

    private static List<Task> createNextNotYetAssignedTasks(List<Task> notYetAssignedTasks, List<Task> assignedTasks) {
        return notYetAssignedTasks.stream()
                .filter(t -> !assignedTasks.contains(t))
                .collect(Collectors.toList());
    }

    /**
     * 目標数を超えないように項目数が多い順に仕事を割り当てる。ただし、最初の1個目が目標数を超える場合はそれを割り当てる
     *
     * @param notYetAssignedTasks 未アサインの仕事
     * @param targetQuantity      目標数
     * @return 割り当てられた仕事
     */
    private static List<Task> assign(List<Task> notYetAssignedTasks, int targetQuantity) {
        int assignedItemTotal = 0;
        List<Task> assignedTasks = new ArrayList<>();

        for (Task task : notYetAssignedTasks) {
            int estimated = assignedItemTotal + task.getItemTotal();
            if (targetQuantity < estimated) {
                if (assignedItemTotal == 0) {
                    assignedTasks.add(task);
                    break;
                }
                continue;
            }
            assignedItemTotal = estimated;
            assignedTasks.add(task);
        }

        return assignedTasks;
    }


    /**
     * 仕事を表すクラス
     */
    private static class Task {
        /**
         * 名前
         */
        private final String name;
        /**
         * 項目数
         */
        private final int itemTotal;

        private Task(String name, int itemTotal) {
            this.name = name;
            this.itemTotal = itemTotal;
        }

        public String getName() {
            return name;
        }

        public int getItemTotal() {
            return itemTotal;
        }
    }
}

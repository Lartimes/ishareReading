package org.ishareReading.bankai.schedul;


import org.ishareReading.bankai.model.HotBook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class TopK {

    private int k = 0;

    private Queue<HotBook> queue;

    public TopK(int k,Queue<HotBook> queue){
        this.k = k;
        this.queue = queue;
    }

    public void add(HotBook hotVideo) {
        if (queue.size() < k) {
            queue.add(hotVideo);
        } else if (queue.peek().getHot() < hotVideo.getHot()){
            queue.poll();
            queue.add(hotVideo);
        }
    }


    public List<HotBook> get(){
        final ArrayList<HotBook> list = new ArrayList<>(queue.size());
        while (!queue.isEmpty()) {
            list.add(queue.poll());
        }
        Collections.reverse(list);
        return list;
    }
}
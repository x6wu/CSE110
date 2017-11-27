package com.teamruse.rarerare.tritontravel;

import java.util.List;

/**
 * Created by Xinyu on 11/6/2017.
 */

public interface DirectionGeneratorListener {
    void onGenerateStart();
    void onGenerateSuccess(List<Path> paths);
}

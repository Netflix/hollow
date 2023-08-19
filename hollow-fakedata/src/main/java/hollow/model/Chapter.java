package hollow.model;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.List;

@HollowPrimaryKey(fields = "chapterId")
public class Chapter {
    ChapterId chapterId;
    ChapterInfo chapterInfo;
    List<Scene> scenes;

    public Chapter(ChapterId chapterId, ChapterInfo chapterInfo, List<Scene> scenes) {
        this.chapterId = chapterId;
        this.chapterInfo = chapterInfo;
        this.scenes = scenes;
    }
}

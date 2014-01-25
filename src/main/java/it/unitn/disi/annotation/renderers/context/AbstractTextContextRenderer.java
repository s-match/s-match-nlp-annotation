package it.unitn.disi.annotation.renderers.context;

import it.unitn.disi.annotation.data.INLPContext;
import it.unitn.disi.annotation.data.INLPNode;
import it.unitn.disi.smatch.renderers.context.BaseFileContextRenderer;
import it.unitn.disi.smatch.renderers.context.ContextRendererException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

/**
 * Abstract class for renderers of context in text format.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class AbstractTextContextRenderer extends BaseFileContextRenderer<INLPContext> implements INLPContextRenderer {

    private static final Logger log = LoggerFactory.getLogger(AbstractTextContextRenderer.class);

    public static final String TRAIN_FILES = "Train files (*.train)";
    private long renderedCount;

    @Override
    protected void process(INLPContext context, BufferedWriter out) throws IOException, ContextRendererException {
        renderedCount = 0;
        INLPNode curNode = context.getRoot();
        processNode(curNode, out);
        if (log.isInfoEnabled()) {
            log.info("Rendered labels: " + renderedCount);
        }
    }

    protected void processNode(INLPNode curNode, BufferedWriter out) throws IOException {
        String toWrite = getTrainSample(curNode);
        if (null != toWrite) {
            out.write(toWrite);
            out.write("\n");
            renderedCount++;
        }
        Iterator<INLPNode> i = curNode.getChildren();
        while (i.hasNext()) {
            processNode(i.next(), out);
        }
    }

    protected abstract String getTrainSample(INLPNode curNode);

    public String getDescription() {
        return TRAIN_FILES;
    }
}
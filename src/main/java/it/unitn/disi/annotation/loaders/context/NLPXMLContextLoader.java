package it.unitn.disi.annotation.loaders.context;

import it.unitn.disi.annotation.data.INLPContext;
import it.unitn.disi.annotation.loaders.context.handlers.NLPXMLContentHandler;
import it.unitn.disi.smatch.loaders.context.BaseXMLContextLoader;
import it.unitn.disi.smatch.loaders.context.ContextLoaderException;
import it.unitn.disi.smatch.loaders.context.handlers.BaseXMLContentHandler;
import it.unitn.disi.smatch.oracles.ILinguisticOracle;

/**
 * Loads SimpleXML with labels.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class NLPXMLContextLoader extends BaseXMLContextLoader<INLPContext> implements INLPContextLoader {

    private final ILinguisticOracle oracle;

    public NLPXMLContextLoader() throws ContextLoaderException {
        super(true);
        this.oracle = null;
    }

    public NLPXMLContextLoader(ILinguisticOracle oracle) throws ContextLoaderException {
        super(true);
        this.oracle = oracle;
    }

    public NLPXMLContextLoader(ILinguisticOracle oracle, boolean uniqueStrings) throws ContextLoaderException {
        super(uniqueStrings);
        this.oracle = oracle;
    }

    @Override
    protected BaseXMLContentHandler<INLPContext> getContentHandler() {
        return new NLPXMLContentHandler(oracle, isUniqueStrings());
    }
}
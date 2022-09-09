package com.readonlydev.updates.util;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

public class Processor
{

//    static class CustomLinkResolver implements HtmlLinkResolver
//    {
//
//        public CustomLinkResolver(HtmlNodeConverterContext context)
//        {
//        }
//
//        @Override
//        public ResolvedLink resolveLink(Node node, HtmlNodeConverterContext context, ResolvedLink link)
//        {
//            if (link.getUrl().startsWith("http:"))
//            {
//                return link.withUrl("https:" + link.getUrl().substring("http:".length()));
//            }
//            return link;
//        }
//
//        static class Factory implements HtmlLinkResolverFactory
//        {
//
//            @Nullable
//            @Override
//            public Set<Class<?>> getAfterDependents()
//            {
//                return null;
//            }
//
//            @Nullable
//            @Override
//            public Set<Class<?>> getBeforeDependents()
//            {
//                return null;
//            }
//
//            @Override
//            public boolean affectsGlobalScope()
//            {
//                return false;
//            }
//
//            @Override
//            public HtmlLinkResolver apply(HtmlNodeConverterContext context)
//            {
//                return new CustomLinkResolver(context);
//            }
//        }
//    }
//
//    static class HtmlConverterTextExtension implements FlexmarkHtmlConverter.HtmlConverterExtension
//    {
//
//        public static HtmlConverterTextExtension create()
//        {
//            return new HtmlConverterTextExtension();
//        }
//
//        @Override
//        public void rendererOptions(@NotNull MutableDataHolder options)
//        {
//        }
//
//        @Override
//        public void extend(FlexmarkHtmlConverter.@NotNull Builder builder)
//        {
//            builder.linkResolverFactory(new CustomLinkResolver.Factory());
//            builder.htmlNodeRendererFactory(new CustomHtmlNodeConverter.Factory());
//        }
//    }
//
//    static class CustomHtmlNodeConverter implements HtmlNodeRenderer
//    {
//
//        public CustomHtmlNodeConverter(DataHolder options)
//        {
//
//        }
//
//        @Override
//        public Set<HtmlNodeRendererHandler<?>> getHtmlNodeRendererHandlers()
//        {
//            return new HashSet<>(Collections.singletonList(new HtmlNodeRendererHandler<>("kbd", Element.class, this::processKbd)));
//        }
//
//        private void processKbd(Element node, HtmlNodeConverterContext context, HtmlMarkdownWriter out)
//        {
//            out.append("<<");
//            context.renderChildren(node, false, null);
//            out.append(">>");
//        }
//
//        static class Factory implements HtmlNodeRendererFactory
//        {
//
//            @Override
//            public HtmlNodeRenderer apply(DataHolder options)
//            {
//                return new CustomHtmlNodeConverter(options);
//            }
//        }
//    }

    public static String parse(final String htmlString)
    {
        return FlexmarkHtmlConverter.builder().build() .convert(htmlString);
    }
}

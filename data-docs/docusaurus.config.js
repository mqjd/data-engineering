// @ts-check
// `@type` JSDoc annotations allow editor autocompletion and type checking
// (when paired with `@ts-check`).
// There are various equivalent ways to declare your Docusaurus config.
// See: https://docusaurus.io/docs/api/docusaurus-config

import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: '数据工程',
  tagline: 'Data is so cool',
  favicon: 'img/favicon.ico',

  url: 'https://mqjd.github.io',
  baseUrl: '/data-engineering',

  organizationName: 'mqjd', // Usually your GitHub org/user name.
  projectName: 'data-engineering', // Usually your repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  i18n: {
    defaultLocale: 'cn',
    locales: ['cn', 'en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: './sidebars.js',
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
              'https://github.com/mqjd/data-engineering/tree/main/data-docs',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
              'https://github.com/mqjd/data-engineering/tree/main/data-docs',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      }),
    ],
  ],

  themeConfig:
      ({
        tableOfContents: {
          minHeadingLevel: 2,
          maxHeadingLevel: 5,
        },
        // Replace with your project's social card
        image: 'img/docusaurus-social-card.jpg',
        navbar: {
          title: '数据工程',
          logo: {
            alt: 'data engineering',
            src: 'img/logo.svg',
          },
          items: [
            {
              type: 'docSidebar',
              sidebarId: 'clusterSidebar',
              label: '集群',
              position: 'left'
            },
            {
              type: 'docSidebar',
              sidebarId: 'hadoopSidebar',
              label: 'Hadoop',
              position: 'left'
            },
            {
              type: 'docSidebar',
              sidebarId: 'sparkSidebar',
              label: 'Spark',
              position: 'left'
            },
            {
              type: 'docSidebar',
              sidebarId: 'flinkSidebar',
              label: 'Flink',
              position: 'left'
            },
            {to: '/blog', label: '博客', position: 'left'},
            {
              href: 'https://github.com/mqjd/data-engineering',
              label: 'GitHub',
              position: 'right',
            },
          ],
        },
        footer: {
          style: 'dark',
          links: [
            {
              title: 'Docs',
              items: [
                {
                  label: 'Apache Hadoop',
                  to: 'https://hadoop.apache.org/',
                },
                {
                  label: 'Apache Spark',
                  to: 'https://spark.apache.org/',
                },
                {
                  label: 'Apache Flink',
                  to: 'https://flink.apache.org/',
                },
                {
                  label: 'Apache Kafka',
                  to: 'https://kafka.apache.org/',
                },
                {
                  label: 'Apache Hive',
                  to: 'https://hive.apache.org/',
                },
                {
                  label: 'Apache HBase',
                  to: 'https://hbase.apache.org/',
                },
                {
                  label: 'Apache Zookeeper',
                  to: 'https://zookeeper.apache.org/',
                }
              ],
            },
            {
              title: 'Community',
              items: [
                {
                  label: 'Docusaurus Stack Overflow',
                  href: 'https://stackoverflow.com/questions/tagged/docusaurus',
                },
              ],
            },
            {
              title: 'More',
              items: [
                {
                  label: 'Docusaurus',
                  to: 'https://docusaurus.io/',
                },
                {
                  label: 'ClickHouse',
                  to: 'https://clickhouse.com/docs',
                },
                {
                  label: 'Apache Airflow',
                  to: 'https://airflow.apache.org/',
                },
                {
                  label: 'Elasticsearch',
                  to: 'https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html',
                },
                {
                  label: 'MongoDB',
                  to: 'https://www.mongodb.com/docs/manual/',
                },
              ],
            },
          ],
          copyright: `Copyright © ${new Date().getFullYear()} data-engineering, Inc. Built with Docusaurus.`,
        },
        prism: {
          theme: prismThemes.github,
          darkTheme: prismThemes.dracula,
          additionalLanguages: ['bash'],
        },
      }),
};

export default config;

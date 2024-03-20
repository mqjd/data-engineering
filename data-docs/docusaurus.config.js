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
              'https://github.com/mqjd/data-engineering/tree/main/data-docs/sidebars.js',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
              'https://github.com/mqjd/data-engineering/tree/main/data-docs/blog',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      }),
    ],
  ],

  themeConfig:
      ({
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
              sidebarId: 'welcomeSidebar',
              label: 'Welcome!',
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
                  label: 'test',
                  to: 'https://github.com/mqjd/data-engineering',
                },
              ],
            },
            {
              title: 'Community',
              items: [
                {
                  label: 'Stack Overflow',
                  href: 'https://stackoverflow.com/questions/tagged/docusaurus',
                },
              ],
            },
            {
              title: 'More',
              items: [
                {
                  label: 'Blog',
                  to: '/blog',
                },
                {
                  label: 'GitHub',
                  href: 'https://github.com/facebook/docusaurus',
                },
              ],
            },
          ],
          copyright: `Copyright © ${new Date().getFullYear()} data-engineering, Inc. Built with Docusaurus.`,
        },
        prism: {
          theme: prismThemes.github,
          darkTheme: prismThemes.dracula,
        },
      }),
};

export default config;
